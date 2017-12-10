package io.github.xiewinson.movieinfocrawler.manager

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class OkHttpManager {
    companion object {
        val TOMATO_HOST = "https://www.rottentomatoes.com"
        val TOMATO_2016 = TOMATO_HOST + "/top/bestofrt/?year=2016"

        val IMDB_HOST = "http://www.imdb.com"

        var client = OkHttpClient.Builder().build()

        fun request(req: Request): Response {
            return client.newCall(req).execute()
        }
        fun get(url: String): String? {
            return try {
                client.newCall(Request.Builder().url(url).build()).execute().body()?.string()
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }

        fun getTomato2016(): String? {
            return get(TOMATO_2016)
        }


        fun searchMovieByDouban(name: String): String? {
            return get("https://api.douban.com/v2/movie/search?q=$name")
        }

        fun searchMovieByImdb(name: String): String? {
            return get("$IMDB_HOST/find?q=$name&s=tt")
        }

    }

}