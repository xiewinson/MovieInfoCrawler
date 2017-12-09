package io.github.xiewinson.movieinfocrawler.crawler

import io.github.xiewinson.movieinfocrawler.manager.OkHttpManager
import io.github.xiewinson.movieinfocrawler.model.douban.DoubanMovie
import io.github.xiewinson.movieinfocrawler.util.CrawlerExecutors
import io.github.xiewinson.movieinfocrawler.util.FileUtil
import io.github.xiewinson.movieinfocrawler.util.GsonUtil
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.util.concurrent.CountDownLatch

class ImdbCrawler : ICrawler {

    var excelFile = FileUtil.imdbFile()
    val excelOs = excelFile.outputStream()
    val excel = XSSFWorkbook()
    val sheet = excel.createSheet()

    var imdbList = listOf(
            "Original Name",
            "Name",
            "Url",
            "Rating",
            "Genre"
    )
    val tomatoSheet = XSSFWorkbook(FileUtil.tomatoFile()).getSheetAt(0)

    override fun crawl() {
        val headRow = sheet.createRow(0)
        for ((index, item) in imdbList.withIndex()) {
            headRow.createCell(index).setCellValue(item)
        }

        val counter = CountDownLatch(tomatoSheet.physicalNumberOfRows)
        for ((index, row) in tomatoSheet.rowIterator().withIndex()) {
            if (index == 0) {
                counter.countDown()
                continue
            }
            val movieName = row.getCell(0).stringCellValue

            CrawlerExecutors.fixedExecute {
                println("正在抓IMDB电影:${index}/${tomatoSheet.physicalNumberOfRows - 1}")
                try {
                    handle(movieName, index, tomatoSheet.physicalNumberOfRows - 1)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    counter.countDown()
                }
            }

        }
        counter.await()
        excel.write(excelOs)
        excelOs.close()
        println("抓取IMDB数据完成")

    }

    private fun handle(movieName: String, index: Int, count: Int) {
        if (movieName.isEmpty()) return
        val result = OkHttpManager.searchMovieByDouban(movieName)
        val doubanMovie = GsonUtil.gson.fromJson(result, DoubanMovie::class.java)
        val hasData = doubanMovie?.subjects != null && doubanMovie.subjects.isNotEmpty()
        synchronized(DoubanCrawler::class.java) {
            val row = sheet.createRow(index)
            row.createCell(0).setCellValue(movieName)
            if (hasData) {
                val first = doubanMovie.subjects.first()
                row.createCell(1).setCellValue(first.title)
                row.createCell(2).setCellValue(first.alt)
                row.createCell(3).setCellValue("${first.rating.average}/${first.rating.max}")
                row.createCell(4).setCellValue(first.genres.toString().replace("[", "").replace("]", ""))
            }
        }
        println(doubanMovie.toString())
    }
}