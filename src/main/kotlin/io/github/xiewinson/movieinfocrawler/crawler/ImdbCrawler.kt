package io.github.xiewinson.movieinfocrawler.crawler

import io.github.xiewinson.movieinfocrawler.manager.OkHttpManager
import io.github.xiewinson.movieinfocrawler.util.CrawlerExecutors
import io.github.xiewinson.movieinfocrawler.util.FileUtil
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.jsoup.Jsoup
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
            "Genre",
            "Country",
            "Cumulative Worldwide Gross",
            "Gross USA",
            "Plot Keywords"
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
                    handle(movieName, index)
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

    private fun handle(movieName: String, index: Int) {
//        if (index > 1) return
        if (movieName.isEmpty()) return

        val qHtml = OkHttpManager.searchMovieByImdb(movieName)
        val a = Jsoup
                .parse(qHtml)
                .select("table.findList tr")
                .first()
                .select("td.result_text a")

        val url = OkHttpManager.IMDB_HOST + a.attr("href")
        val jsoup = Jsoup.parse(OkHttpManager.get(url))

        synchronized(DoubanCrawler::class.java) {
            val row = sheet.createRow(index)
            row.createCell(0).setCellValue(movieName)
            //Name
            row.createCell(1).setCellValue(
                    jsoup.select("div.title_wrapper h1")
                            .text()
                            .replace("(2016)", ""))

            //Url
            row.createCell(2).setCellValue(url)

            //Rating
            row.createCell(3).setCellValue(
                    jsoup.select("div.ratingValue span").text().replace(" ", "")
            )

            //Genre
            row.createCell(4).setCellValue(
                    jsoup.select("h4:contains(Genres:)").next().text()
            )

            //Country
            row.createCell(5).setCellValue(
                    jsoup.select("h4:contains(Country:)").next().text()
            )

            //Gross USA
            row.createCell(6).setCellValue(
                    jsoup.select("h4:contains(Gross USA:)")
                            .first()
                            .parent()
                            .text()
                            .replace("Gross USA: ", "")
            )

            //Cumulative Worldwide Gross
            row.createCell(7).setCellValue(
                    jsoup.select("h4:contains(Cumulative Worldwide Gross:)")
                            .first()
                            .parent()
                            .text()
                            .replace("Cumulative Worldwide Gross: ", "")
            )

            //Plot Keywords
            row.createCell(8).setCellValue(
                    jsoup.select("div[itemprop=keywords] > a").text()
            )

        }
    }
}