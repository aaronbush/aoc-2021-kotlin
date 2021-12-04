package day1

import readInput

fun main() {
    fun part1(input: List<Int>): Int {
        return input.zipWithNext().filter { it.second > it.first }.size
    }

    val input = readInput("Day01_test").map { it.toInt() }
    println("day2.day2.day3.part1: ${part1(input)}")

    // consider window()
    val triples = input.zip(input.drop(1)).zip(input.drop(2))
    println(triples)
    val tripleSums = triples.map { l -> l.first.first + l.first.second + l.second }

    println("day2.day2.part2: ${part1(tripleSums)}")
}