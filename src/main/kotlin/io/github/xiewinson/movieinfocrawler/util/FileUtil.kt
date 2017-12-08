package io.github.xiewinson.movieinfocrawler.util

import java.io.File

class FileUtil {
    companion object {
        fun excelFile(name: String) : File {
            return File("dst", name)
        }
    }
}