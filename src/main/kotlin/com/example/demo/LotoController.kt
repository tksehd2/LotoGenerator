package com.example.demo

import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import java.io.InputStreamReader

@Controller
class LotoController{

    @GetMapping("/")
    fun index(model: Model): String = "index"

    @GetMapping("/analytics")
    fun analytics(model: Model): String {
        model["rateTable"] = LotoSix.sortedRateTable.entries
        model["averageRate"] = LotoSix.averageRate
        model["rateDistribution"] = LotoSix.rateDistribution.entries
        return "analytics"
    }

    @GetMapping("/history")
    fun history(model: Model):String {
        model["prizedItems"] = LotoSix.prizedItems
        return "history"
    }

    @GetMapping("/generate")
    fun generate(model: Model):String{
        val result = LotoSix.pickNumber(81,85)
        model["info"] = true
        model["result"] = result
        return "index"
    }
}