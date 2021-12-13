package day13

import readInput

fun main() {
    val input = readInput("Day13")

    val grid = loadGrid(input)
//    println(part1(grid, listOf(FoldAboutY(7), FoldAboutX(5))))
    println(
        part1(
            grid, listOf(
                FoldAboutX(655),
                FoldAboutY(447),
                FoldAboutX(327),
                FoldAboutY(223),
                FoldAboutX(163),
                FoldAboutY(111),
                FoldAboutX(81),
                FoldAboutY(55),
                FoldAboutX(40),
                FoldAboutY(27),
                FoldAboutY(13),
                FoldAboutY(6),
            )
        )
    )
    grid.print()

}

fun part1(grid: Grid, folds: List<FoldAbout>): Int {
    folds.forEach {
        when (it) {
            is FoldAboutX -> grid.foldAbout(it)
            is FoldAboutY -> grid.foldAbout(it)
        }
//        println("rotated about $it and grid is now ${grid.points.size}")
    }
    return grid.points.count { (_, v) -> v.isNotEmpty() }
}

fun loadGrid(input: List<String>): Grid {
    return Grid(input.associate { line ->
        val x = line.split(",")[0].toInt()
        val y = line.split(",")[1].toInt()
        Location(x, y) to "#"
    }.toMutableMap())
}

data class Location(val x: Int, val y: Int) {
    fun flippedAboutY(fold: Int) = this.copy(y = y - (y - fold) * 2)
    fun flippedAboutX(fold: Int) = this.copy(x = x - (x - fold) * 2)
}

sealed class FoldAbout(val at: Int)
data class FoldAboutX(val x_fold: Int) : FoldAbout(x_fold)
data class FoldAboutY(val y_fold: Int) : FoldAbout(y_fold)

data class Grid(val points: MutableMap<Location, String>) {
    fun foldAbout(fold: FoldAboutY) {
        val belowTheFold =
            points.filter { (l, _) -> l.y > fold.y_fold }.map { (k, v) -> k.flippedAboutY(fold.y_fold) to v }

        // update overlap
        belowTheFold.forEach { (location, dot) -> points[location] = dot }

        // remove old points that were folded

        points.filter { (l, _) -> l.y > fold.y_fold }.forEach { (k, _) -> points.remove(k) }
    }

    fun foldAbout(fold: FoldAboutX) {
        val belowTheFold =
            points.filter { (l, _) -> l.x > fold.x_fold }.map { (k, v) -> k.flippedAboutX(fold.x_fold) to v }

        // update overlap
        belowTheFold.forEach { (location, dot) -> points[location] = dot }

        // remove old points that were folded

        points.filter { (l, _) -> l.x > fold.x_fold }.forEach { (k, _) -> points.remove(k) }
    }

    fun print() {
        val maxX = points.keys.maxOf { it.x } + 1
        val maxY = points.keys.maxOf { it.y } + 1

        repeat(maxY) { y ->
            repeat(maxX) { x ->
                if (points[Location(x, y)] == null) {
                    print(" ")
                } else {
                    print("*")
                }
            }
            println()
        }
    }
}