package day3

import readInput

fun main() {
    val input = readInput("Day03")
    println("p1: ${part1(input)}")
    println("p2: ${part2(input)}")
}

fun part1(input: List<String>): Int {
    val nums = sortedMapOf<Int, Pair<Int, Int>>()
    for (line in input) {
        line.forEachIndexed { pos, bit ->
            nums.compute(pos) { _, v ->
                if (v == null) {
                    if (bit == '0') {
                        1 to 0
                    } else {
                        0 to 1
                    }
                } else {
                    if (bit == '0') {
                        v.first + 1 to v.second
                    } else {
                        v.first to v.second + 1
                    }
                }
            }
        }
    }
    println("nums = ${nums}")
    val gamma = nums.map { maxOf(it.value) }.joinToString(separator = "")
    val epsilon = nums.map { minOf(it.value) }.joinToString(separator = "")
    println("gamma = ${gamma}")
    println("epsilon = ${epsilon}")
    val gamma_int = gamma.toInt(2)
    val epsilon_int = epsilon.toInt(2)

    return gamma_int * epsilon_int
}

fun part2(input: List<String>): Int {
    //oxygen
    var columns = transpose(input)
    reduceColumns(columns, ::leastCommonPositions)
    val oxygen = joinColumns(columns).toInt(2)
    println("oxygen = ${oxygen}")

    // co2
    columns = transpose(input)
    reduceColumns(columns, ::mostCommonPositions)
    val co2 = joinColumns(columns).toInt(2)
    println("co2 = ${co2}")

    return oxygen * co2
}

fun mostCommonPositions(bits: List<Char>): List<Int> {
    val zeroCount = bits.count { it == '0' }
    val oneCount = bits.size - zeroCount

    return if (zeroCount > oneCount) {
        bits.mapIndexedNotNull { index, c -> if (c == '0') index else null }
    } else {
        bits.mapIndexedNotNull { index, c -> if (c == '1') index else null }
    }
}

fun leastCommonPositions(bits: List<Char>): List<Int> {
    val r = bits.indices.toList()
    val r2 = mostCommonPositions(bits)
    return r - r2.toSet()
}

fun transpose(input: List<String>): List<MutableList<Char>> {
    val width = input[0].length - 1

    val columns = buildList<MutableList<Char>> {
        for (i in 0..width) {
            this += mutableListOf<Char>()
        }
        input.forEach { line ->
            line.forEachIndexed { index, c -> this[index] += c }
        }
    }
    return columns
}

fun reduceColumns(columns: List<MutableList<Char>>, f: (List<Char>) -> List<Int>) {
    val width = columns.size
    for (column in 0..width) {
        if (columns[0].size == 1) break

//        println("columns = ${columns}")
        val pruneRows = f(columns[column]).reversed()
//        println("pruneRows = ${pruneRows}")
        pruneRows.forEach { prunePosition(it, columns) }
//        println("columns = ${columns}")
    }
}

fun prunePosition(pos: Int, l: List<MutableList<Char>>) {
    l.forEach { column ->
        column.removeAt(pos)
    }
}

fun joinColumns(l: List<List<Char>>): String {
    var result = ""
    for (cl in l) {
        result += cl[0]
    }
    return result
}

fun maxOf(p: Pair<Int, Int>) =
    if (p.first > p.second) {
        0
    } else {
        1
    }

fun minOf(p: Pair<Int, Int>) =
    if (p.first < p.second) {
        0
    } else {
        1
    }