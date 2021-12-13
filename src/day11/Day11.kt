package day11

import readInput

fun main() {
    val input = readInput("Day11")
    println(part1(makeGrid(input)))
    println(part2(makeGrid(input)))
}

fun makeGrid(input: List<String>): Grid {
    val cells = input.flatMapIndexed { lineNumber, line ->
        line.mapIndexed { columnNumber, char ->
            Location(lineNumber, columnNumber) to char.digitToInt()
        }
    }.associateBy(fun(p: Pair<Location, Int>) = p.first) { it.second }.toMutableMap()
    return Grid(cells)
}

fun part1(grid: Grid): Int {
    var flashed = 0
    repeat(100) { n ->
        grid.update { it + 1 }
        flashed += grid.go()
//        println("flashed = $n / ${flashed}")
//        println("grid = ${grid}")
    }
    return flashed
}

fun part2(grid: Grid): Long {
    val numCells = grid.cells.size
    var numIters = 0L
    while (true) {
        numIters += 1
        grid.update { it + 1 }
        val flashed = grid.go()
        if (flashed == numCells) {
            println("grid = ${grid}")
            break
        }
    }
    return numIters
}

data class Location(val row: Int, val column: Int) {
    val neighbors: Set<Location>
        get() = setOf(north, south, east, west, north_east, south_east, south_west, north_west)

    val north: Location
        get() = Location(row - 1, column)
    val north_east: Location
        get() = Location(row - 1, column + 1)

    val east: Location
        get() = Location(row, column + 1)
    val south_east: Location
        get() = Location(row + 1, column + 1)

    val south: Location
        get() = Location(row + 1, column)
    val south_west: Location
        get() = Location(row + 1, column - 1)

    val west: Location
        get() = Location(row, column - 1)
    val north_west: Location
        get() = Location(row - 1, column - 1)
}

data class Grid(val cells: MutableMap<Location, Int>) {
    private val width = cells.maxOf { it.key.column }
    private val height = width
    private val flashPoint = 10

    private fun inBoundary(location: Location) =
        location.let { it.column in 0..width && it.row in 0..height }

    fun update(fn: (Int) -> Int) {
        cells.replaceAll { _, v -> fn(v) }
    }

    fun go(): Int {
        fun loop(location: Location) {
            // if this one is at flashPoint; update neighbors
            if (cells[location]!! == flashPoint) {
                cells[location] = cells[location]!! + 1
                val next = location.neighbors.filter(::inBoundary).filter { cells[it]!! < flashPoint }
                if (next.isNotEmpty()) {
//                    println("next = ${next}")
                    next.map { l -> cells.computeIfPresent(l) { _, n -> n + 1 } }
//                    println("this = ${this}")
                    next.forEach { loop(it) }
                }
            }
        }
        cells.filter { (_, v) -> v == 10 }.forEach { (k, _) -> loop(k) }
        val flashed = cells.filterValues { v -> v > flashPoint }
        val numFlashed = flashed.size
        flashed.forEach { (l, _) -> cells[l] = 0 }
        return numFlashed
    }
}