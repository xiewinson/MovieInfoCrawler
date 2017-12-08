package io.github.xiewinson.movieinfocrawler.crawler

import io.github.xiewinson.movieinfocrawler.manager.OkHttpManager
import io.github.xiewinson.movieinfocrawler.util.FileUtil
import io.github.xiewinson.movieinfocrawler.util.CrawlerExecutors
import org.apache.poi.xssf.usermodel.XSSFRow
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.jsoup.Jsoup
import org.jsoup.nodes.TextNode
import java.util.concurrent.CountDownLatch

class TomatoCrawler : ICrawler {

    var excelFile = FileUtil.excelFile("tomato.xlsx")
    val excelOs = excelFile.outputStream()
    val excel = XSSFWorkbook()
    val tomatoSheet = excel.createSheet("tomato")


    var tomatoList = listOf(
            "Name",
            "Url",
            "TomatoMeter",
            "Average Rating",
            "Audience Score",
            "Average Rating",
            "Genre",
            "Box Office:",
            "Directed By",
            "Written By:",
            "Studio:"
    )


    override fun crawl() {
        tomatoList()
    }

    private fun tomatoList() {
        val headRow = tomatoSheet.createRow(0)
        for ((index, item) in tomatoList.withIndex()) {
            headRow.createCell(index).setCellValue(item)
        }
        val jsoup = Jsoup.parse(OkHttpManager.getTomato2016())

        val elems = jsoup.select("a[class=unstyled articleLink]:contains((2016))")
        val counter = CountDownLatch(elems.size);
        elems.forEachIndexed { index, it ->
            val row = tomatoSheet.createRow(index + 1)
            row.createCell(0).setCellValue(it.text())
            val url = OkHttpManager.TOMATO_HOST + it.attr("href")
            row.createCell(1).setCellValue(url)

            CrawlerExecutors.fixedExecute {
                println("正在抓取烂番茄电影:${index + 1}/${elems.size}")
                tomatoDetail(url, row)
                counter.countDown()
            }
        }

        counter.await()
        excel.write(excelOs)
        excelOs.close()

        println("抓取烂番茄数据完成")
    }


    private fun tomatoDetail(detailUrl: String, row: XSSFRow) {
        val html = OkHttpManager.get(detailUrl) ?: return

        val jsoup = Jsoup.parse(html)

        //All Critics
        val allCriticsElem = jsoup.select("#all-critics-numbers")
        row.createCell(2).setCellValue(
                allCriticsElem
                        .select("span.meter-value")
                        .text()
        )

        //Average Rating
        row.createCell(3).setCellValue((
                allCriticsElem
                        .select("span[class=subtle superPageFontColor]")
                        .first()
                        .nextSibling() as TextNode)
                .text()
                .trim()

        )

        //audience-score
        val audienceScoreElem = jsoup.select(".audience-score")
        row.createCell(4).setCellValue(
                audienceScoreElem
                        .select("span.superPageFontColor")
                        .text()
        )

        //Average Rating
        row.createCell(5).setCellValue((
                jsoup.select("div.audience-info span")
                        .first()
                        .nextSibling() as TextNode)
                .text()
                .trim()
        )

        //Genre
        row.createCell(6).setCellValue(
                jsoup.select("div[class=meta-label subtle]")
                        .select("div:contains(Genre:)")
                        .next()
                        .text()
        )

        //Box Office:
        row.createCell(7).setCellValue(
                jsoup.select("div[class=meta-label subtle]")
                        .select("div:contains(Box Office:)")
                        .next()
                        .text()
        )

        //Directed By:
        row.createCell(8).setCellValue(
                jsoup.select("div[class=meta-label subtle]")
                        .select("div:contains(Directed By:)")
                        .next()
                        .text()
        )

        //Written By:
        row.createCell(9).setCellValue(
                jsoup.select("div[class=meta-label subtle]")
                        .select("div:contains(Written By:)")
                        .next()
                        .text()
        )

        //Studio:
        row.createCell(10).setCellValue(
                jsoup.select("div[class=meta-label subtle]")
                        .select("div:contains(Studio:)")
                        .next()
                        .text()
        )
    }
}