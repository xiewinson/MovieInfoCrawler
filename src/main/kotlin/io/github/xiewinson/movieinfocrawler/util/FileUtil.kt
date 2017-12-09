package io.github.xiewinson.movieinfocrawler.util

import java.io.File

class FileUtil {
    companion object {
        fun excelFile(name: String): File {
            return File("dst", name)
        }

        fun tomatoFile(): File {
            return excelFile("tomato.xlsx")
        }

        fun doubanFile(): File {
            return excelFile("douban.xlsx")
        }

        fun imdbFile(): File {
            return excelFile("imdb.xlsx")
        }
    }
}