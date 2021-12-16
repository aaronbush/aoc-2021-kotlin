package day14

import readInput

fun main() {
    val input = readInput("Day14")
    val rules = loadInput(input)
//    println(part2("NNCB", 10, rules))
    println(part2("SCVHKHVSHPVCNBKBPVHV", 40, rules))
}

fun part1(starter: String, times: Int, rules: Map<String, ElementRule>): Int {
    var str = starter
    repeat(times) {
        str = str.windowed(2).joinToString(separator = "") { rules[it]!!.combined } + starter.last()
        println("it = ${it}/$str")
        val chunks = str.chunked(3).groupBy { k -> k }.map { (k, v) -> k to v.size }
        println("chunks = ${chunks}")
//        println(str.chunked(3))
    }
//    val chars = str.toCharArray().groupBy { it }
//    val delta = chars.maxOf { (_, v) -> v.size } - chars.minOf { (_, v) -> v.size }
//    println("chars = ${chars}")
    return 0
}

fun part2(starter: String, times: Int, rules: Map<String, ElementRule>): Long {
    // a diff way.  maybe dive n-times into the resulting 2-char pairs and accum along the way?
    // also seeing repetition after specific pattern is applied XY -> Y
    // what do we need to keep if we dive deep into 2-char result?
    var seen = mutableMapOf<String, Long>()
    val lastLetter = starter.last().toString()
    starter.windowed(2).forEach {
        // totally forgot to apply accumulate logic to the initial starter.  should have made a fun for this.
        seen.compute(it) { _, v ->
            if (v == null) 1 else v + 1
        }
    }

    repeat(times) {
        val newSeen = mutableMapOf<String, Long>()
        seen.forEach { (k, n) ->
            val nextTwo =
                listOf("${k.first()}${rules[k]!!.insertedElement}", "${rules[k]!!.insertedElement}${k.last()}")
            newSeen.compute(nextTwo[0]) { _, v -> if (v == null) n else v + n }
            newSeen.compute(nextTwo[1]) { _, v -> if (v == null) n else v + n }
        }
        seen = newSeen
        println("${it + 1} / seen = ${seen}")
    }

    val counts = splitToChars(seen).toMutableMap()
    counts[lastLetter] = counts[lastLetter]!! + 1 // this seems odd todo: what is missing
    val max = counts.maxOf { (_, v) -> v }
    val min = counts.minOf { (_, v) -> v }
    println("min/max = $min/${max}")
    return max - min
}

fun splitToChars(pairCounts: Map<String, Long>): Map<String, Long> {
    val result = mutableMapOf<String, Long>()
    pairCounts.forEach { (k, v) ->
        result.compute(k.first().toString()) { _, currCount -> if (currCount == null) v else currCount + v }
    }
    println("result = ${result}")
    return result
}

fun loadInput(input: List<String>): Map<String, ElementRule> {
    return input.map { line ->
        "(\\S+) -> (\\S+)".toRegex().matchEntire(line)?.destructured?.let { (pair, insert) ->
            ElementRule(pair, insert)
        } ?: TODO()
    }.associateBy { it.elementPair }
}

data class ElementRule(val elementPair: String, val insertedElement: String) {
    val combined: String
        get() = elementPair[0] + insertedElement //+ elementPair[1]
}