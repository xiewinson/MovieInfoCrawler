package io.github.xiewinson.movieinfocrawler.model.douban

data class Rating(var max: Int,
                  var min: Int,
                  var average: Float,
                  var stars: String)