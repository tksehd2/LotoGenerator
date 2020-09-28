package com.loto

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LotoApplication

fun main(args: Array<String>) {
    runApplication<LotoApplication>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}
