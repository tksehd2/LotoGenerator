package com.loto

import org.springframework.core.io.ClassPathResource
import java.io.InputStreamReader

object LotoSix {
    private var cachedMap = mutableMapOf<Int, MutableList<Set<Int>>>()
    private val expectNumbers: Set<Int> by lazy {
        var result = mutableSetOf<Int>()
        rateTable.forEach {
            if (it.value < averageRate.toInt()) {
                result.add(it.key)
            }
        }
        result
    }

    // Data Analytics
    val prizedItems: List<LotoItem> by lazy {
        val resource = ClassPathResource("data/prized")
        InputStreamReader(resource.inputStream, "UTF-8").use { reader ->
            reader.readLines().map { line ->
                var split = line.split("\t")
                var numbers = split.subList(2, 8).map { item ->
                    item.toInt()
                }.toSet()

                LotoItem(split[0], numbers, mutableListOf<Float>(), 0.0f)
            }
        }
    }
    private val rateTable: Map<Int, Float> by lazy {
        (1..43).map { i ->
            var count = 0
            for (item in prizedItems) {
                if (item.numbers.contains(i)) {
                    count += 1
                }
            }
            i to (count.toFloat() / prizedItems.size.toFloat()) * 100.0f
        }.toMap()
    }
    private val numberTable: Map<Int, List<Set<Int>>> by lazy {
        val resource = ClassPathResource("data/num_array")
        InputStreamReader(resource.inputStream, "UTF-8").use { reader ->
            var map = mutableMapOf<Int, MutableList<Set<Int>>>()
            for (line in reader.readLines()) {
                val number = line.split(",").map { it.toInt() }.toSet()
                val totalRates = calcTotalRates(number)
                val key = totalRates.toInt()

                if ((number - expectNumbers).size != number.size) {
                    continue
                }
                if (!checkSerialNumber(number, 2)){
                    continue
                }
                if(isPrized(number)){
                    continue
                }

                if (!map.containsKey(key)) {
                    map[key] = mutableListOf()
                }

                map[key]?.add(number)
            }
            map
        }
    }

    // Data Processing
    val sortedRateTable: Map<Int, Float> by lazy { rateTable.toList().sortedBy { it.second }.toMap() }
    val rateDistribution: Map<Int, Int> by lazy {
        var distribution = mutableMapOf<Int, Int>()
        for (i in 70..90) {
            prizedItems.forEach {
                if (i <= it.total_rates && i + 1 > it.total_rates) {
                    distribution[i] = distribution.getOrDefault(i, 0) + 1
                }
            }
        }
        distribution
    }
    val averageRate: Float by lazy {
        (rateTable.values.sum() / rateTable.size)
    }

    // Make Result
    private fun calcPrizedRate() {
        prizedItems.forEach { item ->
            item.rates = item.numbers.map { number ->
                rateTable.getOrDefault(number, 0.0f)
            }
            item.total_rates = item.rates.sum()
        }
    }

    init {
        calcPrizedRate()
    }

    private fun checkSerialNumber(numbers: Set<Int>, lessThen: Int): Boolean {
        var serialCount = 0
        val sorted = numbers.toList().sorted()
        val size = sorted.size - 2
        for (i in 0..size) {
            if (sorted[i] + 1 == sorted[i + 1]) {
                serialCount += 1
            }
        }
        return serialCount <= lessThen
    }

    private fun isPrized(numbers: Set<Int>): Boolean {
        prizedItems.forEach {
            if (it.numbers == numbers)
                return true
        }
        return false
    }

    private fun numberToRates(numbers: Set<Int>): List<Float> {
        return numbers.map { rateTable.getOrDefault(it, 0f) }
    }

    private fun calcTotalRates(numbers: Set<Int>): Float {
        return numbers.sumByDouble { rateTable.getOrDefault(it, 0f).toDouble() }.toFloat()
    }

    fun pickNumber(minRate: Int, maxRate: Int): List<LotoItem> {
        return (minRate..maxRate).map {
            val number = numberTable[it]?.random()!!
            LotoItem("", number, numberToRates(number), calcTotalRates(number))
        }
    }

    // Data Struct
    class LotoItem(var times: String, var numbers: Set<Int>, var rates: List<Float>, var total_rates: Float) {
        override fun toString(): String = "$times, $numbers, $rates, $total_rates"
    }
}
//
//fun main() {
//    LotoSix.pickNumber(85,86)
//}