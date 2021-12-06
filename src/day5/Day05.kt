package day5

import readInput

fun main() {
    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}

fun part1(input: List<String>): Int {
    // filter for horizontal and vertical lines
    val segments = input.mapNotNull { line ->
        "(\\d+),(\\d+)\\s->\\s(\\d+),(\\d+)".toRegex().matchEntire(line)?.destructured?.let { (s_x, s_y, e_x, e_y) ->
            if (s_x == e_x || s_y == e_y) {
                LineSegment(s_x.toInt() to s_y.toInt(), e_x.toInt() to e_y.toInt())
            } else null
        }
    }

    val intersections = intersectionPoints(segments)
//    println(intersections)
    return intersections.size
}

fun part2(input: List<String>): Int {
    val segments = input.mapNotNull { line ->
        "(\\d+),(\\d+)\\s->\\s(\\d+),(\\d+)".toRegex().matchEntire(line)?.destructured?.let { (s_x, s_y, e_x, e_y) ->
            LineSegment(s_x.toInt() to s_y.toInt(), e_x.toInt() to e_y.toInt())
        }
    }
    val intersections = intersectionPoints(segments)
//    println(intersections)
    return intersections.size
}

fun intersectionPoints(segments: List<LineSegment>): Map<Pair<Int, Int>, List<Pair<Int, Int>>> {
    val allPoints = segments.flatMap { line -> line.points() }
    return allPoints.groupBy { it }.filter { (_, v) -> v.size > 1 }
}

data class Fraction(val numerator: Int, val denominator: Int) {
    companion object {
        private fun gcd(n1: Int, n2: Int): Int {
            return if (n2 == 0) n1 else gcd(n2, n1 % n2)
        }

        fun of(numerator: Int, denominator: Int): Fraction {  // a reduced fraction
            val gcd = if (kotlin.math.abs(gcd(numerator, denominator)) == 0) {
                1
            } else {
                kotlin.math.abs(gcd(numerator, denominator))
            }

            return Fraction(numerator / gcd, denominator / gcd)
        }
    }
}

data class LineSegment(val start: Pair<Int, Int>, val end: Pair<Int, Int>) {
    val slope = if (start.second == end.second) { // horizontal line; dx = 1, dy = 0
        Fraction(1, 0)
    } else if (start.first == end.first) { // vertical line; dx = 0, dy = 1
        Fraction(0, 1)
    } else { // diagonal
        Fraction.of(start.first - end.first, start.second - end.second)
    }

    // list of points along line segment; inclusive of each end
    fun points(): List<Pair<Int, Int>> =
        if (slope.denominator == 0) { // horizontal
            (start.first toward end.first by slope.numerator).map { it to start.second }
        } else if (slope.numerator == 0) { // vertical
            (start.second toward end.second by slope.denominator).map { start.first to it }
        } else { // diagonal
            val xs = start.first toward end.first by slope.numerator
            val ys = start.second toward end.second by slope.denominator
            xs.zip(ys)
        }
}

infix fun Int.toward(end: Int): MyProgression {
    return MyProgression(this, end)
}

data class MyProgression(val start: Int, val end: Int) {
    infix fun by(step: Int): IntProgression {
        val resolved_step = if (end > start) kotlin.math.abs(step) else -1 * kotlin.math.abs(step)
        return IntProgression.fromClosedRange(start, end, if (resolved_step == 0) 1 else resolved_step)
    }
}