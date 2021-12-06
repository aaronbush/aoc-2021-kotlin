package day5

import readInput

fun main() {
    val segments = readInput("Day05").asSegments()
    println(part1(segments))
    println(part2(segments))
}

fun List<String>.asSegments(): List<LineSegment> {
    return this.mapNotNull { line ->
        "(\\d+),(\\d+)\\s->\\s(\\d+),(\\d+)".toRegex().matchEntire(line)?.destructured?.let { (s_x, s_y, e_x, e_y) ->
            LineSegment(s_x.toInt() to s_y.toInt(), e_x.toInt() to e_y.toInt())
        }
    }
}

fun part1(segments: List<LineSegment>): Int {
    val h_v_segments = segments.filter { it.slope.numerator == 0 || it.slope.denominator == 0 }
    val intersections = h_v_segments.intersectionPoints()
//    println(intersections)
    return intersections.size
}

fun part2(segments: List<LineSegment>): Int {
    val intersections = segments.intersectionPoints()
//    println(intersections)
    return intersections.size
}

fun List<LineSegment>.intersectionPoints(): Map<Point, List<Point>> {
    val allPoints = flatMap { line -> line.points() }
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

data class LineSegment(val start: Point, val end: Point) {
    val slope = if (start.y == end.y) { // horizontal line; dx = 1, dy = 0
        Fraction(1, 0)
    } else if (start.x == end.x) { // vertical line; dx = 0, dy = 1
        Fraction(0, 1)
    } else { // diagonal
        Fraction.of(start.x - end.x, start.y - end.y)
    }

    // list of points along line segment; inclusive of each end
    fun points(): List<Point> =
        if (slope.denominator == 0) { // horizontal
            (start.x toward end.x by slope.numerator).map { it to start.y }
        } else if (slope.numerator == 0) { // vertical
            (start.y toward end.y by slope.denominator).map { start.x to it }
        } else { // diagonal
            val xs = start.x toward end.x by slope.numerator
            val ys = start.y toward end.y by slope.denominator
            xs.zip(ys)
        }
}

typealias Point = Pair<Int, Int>

val Pair<Int, Int>.x
    get() = this.first
val Pair<Int, Int>.y
    get() = this.second

infix fun Int.toward(end: Int): MyProgression {
    return MyProgression(this, end)
}

data class MyProgression(val start: Int, val end: Int) {
    infix fun by(step: Int): IntProgression {
        val resolved_step = if (end > start) kotlin.math.abs(step) else -1 * kotlin.math.abs(step)
        return IntProgression.fromClosedRange(start, end, resolved_step)
    }
}