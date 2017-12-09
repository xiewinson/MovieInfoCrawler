package io.github.xiewinson.movieinfocrawler.model.douban

data class Subject(
        var rating: Rating,
        var genres: List<String>,
        var title: String,
        var original_title: String,
        var subtype: String,
        var year: String,
        var alt: String,
        var id: String)

