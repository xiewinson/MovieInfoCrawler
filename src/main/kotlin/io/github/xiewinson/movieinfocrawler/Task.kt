package io.github.xiewinson.movieinfocrawler

import io.github.xiewinson.movieinfocrawler.crawler.TomatoCrawler
import io.github.xiewinson.movieinfocrawler.util.CrawlerExecutors

class Task {
    fun start() {
        CrawlerExecutors.singleExecute {
            TomatoCrawler().crawl()
        }
        CrawlerExecutors.singleExecute {
            CrawlerExecutors.shutdown()
        }
    }
}