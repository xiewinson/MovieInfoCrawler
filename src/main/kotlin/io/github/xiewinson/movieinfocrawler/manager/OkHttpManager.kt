package io.github.xiewinson.movieinfocrawler.manager

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class OkHttpManager {
    companion object {
        val TOMATO_HOST = "https://www.rottentomatoes.com"
        val TOMATO_2016 = TOMATO_HOST + "/top/bestofrt/?year=2016"

        var client = OkHttpClient.Builder().build()

        fun request(req: Request): Response {
            return client.newCall(req).execute()
        }

        fun get(url: String): String? {
            return client.newCall(Request.Builder().url(url).build()).execute().body()?.string()
        }

        fun getTomato2016(): String? {
            return get(TOMATO_2016)
        }
    }

}