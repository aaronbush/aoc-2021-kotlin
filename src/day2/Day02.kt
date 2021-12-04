package day2

import readInput

fun main() {
    val input = readInput("Day02_test")
    println(part1(input))
    println(part2(input))
}

fun part2(input: List<String>): Int {
    var fwd = 0
    var depth = 0
    var aim = 0
    input.forEach {
        val (dir, m) = it.split("\\s".toRegex(), 2)
        val magnitude = m.toInt()
        when (dir) {
            "forward" -> {
                fwd += magnitude
                depth += aim * magnitude
            }
            "up" -> aim -= magnitude
            "down" -> aim += magnitude
        }
    }
    println("fwd = $fwd")
    println("depth = $depth")
    return fwd * depth
}

fun part1(input: List<String>): Int {
    val fwd = input.extractValuesFor("forward").sum()
    val down = input.extractValuesFor("down").sum()
    val up = input.extractValuesFor("up").sum()
    println("forward: $fwd")
    println("down: $down")
    println("up: $up")
    return (down - up) * fwd
}

fun List<String>.extractValuesFor(filterString: String): List<Int> {
    return filter { it.startsWith(filterString) }.map { s ->
        s.trim().split("\\s".toRegex(), 2)[1].toInt()
    }
}