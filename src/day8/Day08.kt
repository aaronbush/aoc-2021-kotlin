package day8

import readInput

fun main() {
    val input = readInput("Day08")
    val displayData = parseToDisplays(input)
    println(part1(displayData))
    println(part2(displayData))
}

typealias DisplayLine = Pair<List<String>, List<String>>

fun parseToDisplays(input: List<String>) = input.map { line ->
    val parts = line.split("|")
    parts[0].trim().split(" ") to parts[1].trim().split(" ")
}

fun part1(displayData: List<DisplayLine>): Int {
    val knownDigits = displayData.map { displayLine ->
        val (_, digits) = displayLine
        digits.count { it.length == 2 || it.length == 3 || it.length == 4 || it.length == 7 }
    }
    return knownDigits.sum()
}

fun part2(displayData: List<DisplayLine>): Int {
    return displayData.sumOf { line ->
        val knownDigits = line.first.uniqueDigits()
        val unknownDigits = line.first.filterNot {
            knownDigits.containsValue(it)
        }
        val segment = Segment(knownDigits[1]!!, knownDigits[4]!!, knownDigits[7]!!, knownDigits[8]!!, unknownDigits)
        val displayedValue = segment.valueOf(line.second)
//        println("displayedValue = ${displayedValue}")
        displayedValue
    }
}

fun List<String>.uniqueDigits(): Map<Int, String> {
    // '1' == 2
    // '7' == 3
    // '4' == 4
    // '8' == 7
    return this.filter { it.length == 2 || it.length == 3 || it.length == 4 || it.length == 7 }.associate {
        when (it.length) {
            2 -> 1 to it
            3 -> 7 to it
            4 -> 4 to it
            7 -> 8 to it
            else -> TODO()
        }
    }
}

data class Segment(
    val one: String,
    val four: String,
    val seven: String,
    val eight: String,
    val others: List<String>
) {
    // one way to figure this out.  i'm sure there is a 'simplified' way
    // #1
    // #4
    // #7
    // #8
    // top bar = '7' - '1'
    // #9 == '4' + top-bar + 1 unknown (bottom-bar)
    // #3 == right bars + top bar + bottom-bar + 1-unknown (middle-bar)
    // #6 == top + middle + bottom + 3-unknown (not 9)
    // bottom-right = #6 && #1
    // top-right = #1 - bottom-right
    // #5 (and top-left) == top + middle + bottom + bottom-right + 1 unknown
    // #2 (and bottom-left) = top, mid, bottom, top-right
    // #0 (put it together)

    private var digitCodes = mutableMapOf<String, Int>()
    private var lookup = mutableMapOf<String, Int>()

    operator fun String.minus(other: String): String = toSet().subtract(other.toSet()).joinToString("")

    private fun List<String>.findPartial(match: String, remaining: Int = 1) =
        find {
            it.toSet().subtract(match.toSet()).size == remaining
        } ?: throw UnknownError("couldn't match $match in [$this]")

    private fun String.sort() = toSet().sorted().joinToString("")

    init {
        val unknowns = others.toMutableList()
        digitCodes[one] = 1
        digitCodes[four] = 4
        digitCodes[seven] = 7
        digitCodes[eight] = 8

        val topBar = seven - one

        val nine = unknowns.filter { it.length == 6 }.findPartial(four + topBar)
        digitCodes[nine] = 9
        unknowns -= nine

        val bottomBar = nine - (four + topBar)

        val three = unknowns.findPartial(one + topBar + bottomBar)
        digitCodes[three] = 3
        unknowns -= three

        val middleBar = three - topBar - bottomBar - one

        val six = unknowns.findPartial(topBar + middleBar + bottomBar, 3)
        digitCodes[six] = 6
        unknowns -= six

        val zero = unknowns.filter { it.length == 6 }[0]
        digitCodes[zero] = 0
        unknowns -= zero

        val bottomRight = one.toSet().intersect(six.toSet())

        val five = unknowns.findPartial(topBar + middleBar + bottomBar + bottomRight)
        digitCodes[five] = 5
        unknowns -= five

        digitCodes[unknowns[0]] = 2

//        println("digitCodes = $digitCodes")

        digitCodes.forEach { (key, value) ->
            lookup[key.sort()] = value
        }
    }

    fun valueOf(digits: List<String>) = digits.map { valueOf(it) }.joinToString("").toInt()

    private fun valueOf(digit: String) =
        lookup[digit.sort()] ?: throw UnknownError("couldn't find $digit in $digitCodes")
}