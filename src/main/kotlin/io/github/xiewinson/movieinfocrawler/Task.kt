package io.github.xiewinson.movieinfocrawler

import io.github.xiewinson.movieinfocrawler.crawler.DoubanCrawler
import io.github.xiewinson.movieinfocrawler.crawler.ImdbCrawler
import io.github.xiewinson.movieinfocrawler.util.CrawlerExecutors

class Task {

    fun start() {

//        CrawlerExecutors.singleExecute {
//            TomatoCrawler().crawl()
//        }

//        CrawlerExecutors.singleExecute {
//            DoubanCrawler().crawl()
//        }

        CrawlerExecutors.singleExecute {
            ImdbCrawler().crawl()
        }


        CrawlerExecutors.singleExecute {
            CrawlerExecutors.shutdown()
        }
    }
}