package day7

import readInput
import kotlin.math.abs

// may help: https://en.wikipedia.org/wiki/Central_tendency

fun main() {
    val input = readInput("Day07")[0].split(",").map { it.toLong() }.sorted()
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<Long>): Long {
    //16,1,2,0,4,2,7,1,2,14
    //0,1,1,2,2,2,4,7,14,16
    // median = 2
    val midPoints = if (input.size % 2 == 0) {  // input len is even
        val u = input[(input.size / 2) + 1]
        val d = input[(input.size / 2)]
        println("u = ${u}")
        println("d = ${d}")
        (u to d)
    } else {
        TODO()
    }
    println("midPoints = ${midPoints}")
    return minOf(input.sumOf { abs(it - midPoints.first) },
        input.sumOf { abs(it - midPoints.second) })
}

fun part2(input: List<Long>): Long {
    val min = input.first()
    val max = input.last()
    // for each num sum the distance from it to all others
    val costs = (min..max).map { center ->
        val distances = input.map { (center to it) }.map { (it to partialSum(abs(it.first - it.second))) }
        (center to distances.sumOf { it.second })
    }
    val lowest = costs.minOf { it.second }
//    println("costs = ${costs}")
//    println(costs.find { it.second == lowest })
    return lowest
}

fun partialSum(n: Long): Long = (n * (n + 1)) / 2