package day10

import readInput

fun main() {
    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}

val syntax = Syntax(
    listOf(
        OpeningGlyph('(') to ClosingGlyph(')'),
        OpeningGlyph('{') to ClosingGlyph('}'),
        OpeningGlyph('[') to ClosingGlyph(']'),
        OpeningGlyph('<') to ClosingGlyph('>'),
    )
)

fun part1(input: List<String>): Int {
    val points = mapOf(
        ClosingGlyph(')') to 3,
        ClosingGlyph(']') to 57,
        ClosingGlyph('}') to 1197,
        ClosingGlyph('>') to 25137,
    )

    val openingGlyphs = ArrayDeque<OpeningGlyph>()
    val corruptedLines = mutableSetOf<String>()
    val corruptedEndings = mutableListOf<ClosingGlyph>()

    input.forEachIndexed { lineNum, line ->
        line.map { syntax.glyphFor(it) }
            .forEach { glyph ->
                when (glyph) {
                    is OpeningGlyph -> openingGlyphs.add(glyph)
                    is ClosingGlyph -> {
                        val lastOpening = openingGlyphs.removeLast()
                        if (syntax.other(lastOpening) != glyph) {
//                            println("$lineNum expecting ${syntax.other(lastOpening)} but found $glyph")
                            corruptedLines += line
                            corruptedEndings += glyph
                        }
                    }
                }
            }
    }
    return corruptedEndings.mapNotNull { points[it] }.sum()
}

fun part2(input: List<String>): Long {
    val points = mapOf(
        ClosingGlyph(')') to 1,
        ClosingGlyph(']') to 2,
        ClosingGlyph('}') to 3,
        ClosingGlyph('>') to 4,
    )

    val openingGlyphs = ArrayDeque<OpeningGlyph>()
    val corruptedLines = mutableSetOf<String>()

    input.forEachIndexed { _, line ->
        line.map { syntax.glyphFor(it) }
            .forEach { glyph ->
                when (glyph) {
                    is OpeningGlyph -> openingGlyphs.add(glyph)
                    is ClosingGlyph -> {
                        val lastOpening = openingGlyphs.removeLast()
                        if (syntax.other(lastOpening) != glyph) {
                            corruptedLines += line
                        }
                    }
                }
            }
    }
    val incompleteLines = input - corruptedLines
    val scores = mutableListOf<Long>()
    for (line in incompleteLines) {
        val glyphsToClose = ArrayDeque<OpeningGlyph>()
        line.map { syntax.glyphFor(it) }
            .forEach { glyph ->
                when (glyph) {
                    is OpeningGlyph -> glyphsToClose.add(glyph)
                    is ClosingGlyph -> glyphsToClose.removeLast()
                }
            }
        val closers = glyphsToClose.reversed().map { syntax.other(it) }
        val total = closers.fold(0L) { acc, glyph -> acc * 5 + points[glyph]!! }
        scores += total
        println("closers = ${closers} == $total")
    }
    return scores.sorted()[scores.size / 2]
}

data class Syntax(val glyphs: List<Pair<OpeningGlyph, ClosingGlyph>>) {
    fun glyphFor(char: Char): Glyph {
        val g = glyphs.find { it.first.char == char }
        return g?.first ?: (glyphs.find { it.second.char == char }?.second ?: TODO())
    }

    fun other(glyph: Glyph): Glyph {
        return when (glyph) {
            is OpeningGlyph -> glyphs.find { it.first == glyph }?.second ?: TODO()
            is ClosingGlyph -> glyphs.find { it.second == glyph }?.first ?: TODO()
        }
    }
}

sealed class Glyph(val value: Char)
data class OpeningGlyph(var char: Char) : Glyph(char)
data class ClosingGlyph(var char: Char) : Glyph(char)