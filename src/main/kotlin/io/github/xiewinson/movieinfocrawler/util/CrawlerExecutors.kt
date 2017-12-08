package io.github.xiewinson.movieinfocrawler.util

import java.util.concurrent.Executors

class CrawlerExecutors {
    companion object {
        val fixedPool = Executors.newFixedThreadPool(10)
        val singlePool = Executors.newSingleThreadExecutor()
        fun fixedExecute(r: () -> Unit) {
            fixedPool.execute(r)
        }

        fun singleExecute(r: () -> Unit) {
            singlePool.execute(r)
        }

        fun shutdown() {
            fixedPool.shutdown()
            singlePool.shutdown()
        }
    }
}