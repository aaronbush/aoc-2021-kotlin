package day15

import readInput
import kotlin.math.min

fun main() {
    val input = readInput("Day15")
    val grid = parseInput(input)
    val newGrid = expandGrid(grid, 4)
//    println("newGrid = ${newGrid}")
    println(part1(newGrid))
//    expandGrid(Grid(mapOf(Location(0, 0) to Node(8, Location(0, 0)))), 4)
//    println("grid = ${grid}")
//    println(part1(grid))
}

fun part1(grid: Grid): Int {
    // trying Dijkstra: https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm
    val myQueue = mutableListOf<CalculatedPath>()

    val start = CalculatedPath(Location(0, 0), 0)
    myQueue.add(start)

    grid.nodes.filterNot { it.key == start.location }.map { (_, node) -> myQueue.add(CalculatedPath(node.location)) }

    while (myQueue.isNotEmpty()) {
        val current = myQueue.sortedWith(CalculatedPath.comparator).first()
        myQueue.remove(current)
        val neighbors = grid.neighbors(current.location)
//        println("\ncurrent = ${current}")

        neighbors.forEach { neighbor ->
            val i = myQueue.indexOf(CalculatedPath(neighbor.location))
            if (i >= 0) {
                val totalCost = current.cost + neighbor.riskValue
//                println("cost $totalCost to go from $current to $neighbor")
                val existing = myQueue[i]
                // if new distance is lower update in queue
                if (totalCost < existing.cost) {
                    existing.cost = min(totalCost, existing.cost)
                    grid[existing.location].parent = grid[current.location]
//                    println("updated cost = ${existing}")
                }
            }
        }
    }

    fun pathCost(initial: Int, node: Node?): Int {
        if (node == null || node.location == Location(0, 0)) {
            return initial
        }
        return pathCost(initial + node.riskValue, node.parent)
    }

    return pathCost(grid[grid.end].riskValue, grid[grid.end].parent)
}

fun parseInput(input: List<String>): Grid {
    return Grid(input.flatMapIndexed { x, line ->
        line.mapIndexed { y, char ->
            Node(char.digitToInt(), Location(y, x))
        }
    }.associateBy { it.location })
}

fun expandGrid(grid: Grid, times: Int): Grid {
    val end = grid.end

    operator fun Location.times(n: Int): List<Pair<Location, Int>> {
        val r = (0..n).flatMap { xn ->
            (0..n).map { yn ->
                copy(x = x + xn * (end.x + 1), y = y + yn * (end.y + 1)) to xn + yn
            }
        }
//        println("r = ${r}")
        return r
    }

    /*
        (0,0)  - (0,10)  - (0,20)...
        (10,0) - (10,10) - (20,20)...
        ...
     */
    fun scaleNum(o: Int, by: Int): Int {
        val total = o + by
        return if (total > 9) {
            total - 9
        } else {
            total
        }
    }

    val newGridMap = grid.nodes.flatMap { (ol, on) ->
        val l = ol * times
        l.map { (nl, inc) ->
            Node(scaleNum(on.riskValue, inc), nl)
        }
    }.associateBy { it.location }
    return Grid(newGridMap)
}

data class Location(val x: Int, val y: Int) {
    val north: Location
        get() = copy(y = y - 1)
    val south: Location
        get() = copy(y = y + 1)
    val east: Location
        get() = copy(x = x + 1)
    val west: Location
        get() = copy(x = x - 1)
}

data class CalculatedPath(val location: Location, var cost: Long = Long.MAX_VALUE) {
    companion object {
        val comparator = fun(i1: CalculatedPath, i2: CalculatedPath) = i1.cost.compareTo(i2.cost)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CalculatedPath

        if (location != other.location) return false

        return true
    }

    override fun hashCode(): Int {
        return location.hashCode()
    }

}

data class Node(val riskValue: Int, val location: Location, var parent: Node? = null)

data class Grid(val nodes: Map<Location, Node>) {
    private val max_x = nodes.maxOf { (k, _) -> k.x }
    private val max_y = nodes.maxOf { (k, _) -> k.y }

    val end: Location
        get() = Location(max_x, max_y)

    private val cardinals = listOf(Location::north, Location::south, Location::east, Location::west)

    fun neighbors(location: Location) =
        cardinals.map { it.get(location) }.filter { n ->
            n.x in 0..max_x && n.y in 0..max_y
        }.map { nodes[it]!! }

    operator fun get(location: Location) = nodes[location]!!
}