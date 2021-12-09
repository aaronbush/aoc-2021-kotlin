package day9

import readInput
import java.util.function.Predicate

fun main() {
    val input = readInput("Day09")
    val grid = loadGrid(input)

    println(part1(grid))
    println(part2(grid))
}


fun part1(grid: Grid): Int {
    return lowPoints(grid).sumOf { it.height + 1 }
}

fun part2(grid: Grid): Int {
    val basins = lowPoints(grid).map { lowPoint -> grid.surroundingFilter(lowPoint) { p -> p.height != 9 } }
    val basinSizes = basins.map { it.size }.sortedDescending().take(3)
    val result = basinSizes.reduce { acc, i -> acc * i }
//    println("result = $result")
    return result
}

fun lowPoints(grid: Grid): List<Point> =
    grid.mapAdjacent { centralPoint, adjacentPoints ->
        if (adjacentPoints.all { centralPoint.height < it.height }) centralPoint else null
    }.filterNotNull()

data class Grid(val points: List<Point>) {
    private val maxRows = points.maxOf { it.row }
    private val maxColumns = points.maxOf { it.column }
    private val directions = listOf(Point::north, Point::south, Point::east, Point::west)
    private val pointMap = points.associateBy { p -> (p.row to p.column) }

    fun surroundingFilter(point: Point, p: Predicate<Point>): Set<Point> {
        val result = mutableSetOf<Point>()
        fun loop(nextPoint: Point, accum: MutableSet<Point>): Set<Point> {
            if (p.test(nextPoint) && accum.add(nextPoint)) {
                nextPoint.adjacentPoints().forEach { loop(it, accum) }
            }
            return accum
        }
        return loop(point, result)
    }

    fun <R> mapAdjacent(fn: (currentPoint: Point, adjacentPoints: List<Point>) -> R): List<R> {
        return pointMap.map { (k, v) ->
            val aps = v.adjacentPoints()
            fn(v, aps)
        }
    }

    private fun Point.adjacentPoints(): List<Point> {
        val adjacent = directions.map { fn -> this.let(fn) }
            .filter { it.first in 0..maxRows && it.second in 0..maxColumns }
            .map { pointMap[it.first to it.second] ?: TODO() }
        return adjacent.toList()
    }
}

data class Point(val row: Int, val column: Int, val height: Int) {
    fun north() = row - 1 to column
    fun south() = row + 1 to column
    fun east() = row to column + 1
    fun west() = row to column - 1
}

fun loadGrid(input: List<String>): Grid {
    val rows = input.map { it.toList() }
    val points = rows.flatMapIndexed { rowNum, row ->
        row.mapIndexed { colNum, value ->
            Point(rowNum, colNum, value.digitToInt())
        }
    }
    return Grid(points)
}
