package day12

import readInput

fun main() {
    val input = readInput("Day12")
    val startCave = mapCaves(input)
    println(startCave.visit())
    println(startCave.visit2())
}

fun mapCaves(input: List<String>): StartCave {
    val caves = mutableMapOf("start" to StartCave, "end" to EndCave)
    fun caveForName(name: String): Cave {
        return if (caves[name] == null) {
            if (name[0] in 'a'..'z') {
                val c = SmallCave(name)
                caves[name] = c
                c
            } else {
                val c = LargeCave(name)
                caves[name] = c
                c
            }
        } else {
            caves[name]!!
        }
    }
    input.forEach { line ->
        val left = line.split("-")[0]
        val leftCave = caveForName(left)
        val right = line.split("-")[1]
        val rightCave = caveForName(right)
        leftCave.connectTo(rightCave)
    }

    return (caves["start"] as StartCave?)!!
}

sealed class Cave(val id: String) {
    private val caves = mutableSetOf<Cave>()
    fun connectTo(other: Cave) = apply {
        if (other !is StartCave) {
//            println("connecting $this to $other")
            caves += other
        }
        if (other !is EndCave && this !is StartCave) {
//            println("back connecting $other to $this")
            other.caves += this
        }
    }

    private val connections: Set<Cave>
        get() = caves

    fun visit(): Int {
        val completedPaths = mutableSetOf<List<Cave>>()
        fun loop(nextCave: Cave, visited: List<Cave>): List<Cave> {
//            println("nextCave = [${nextCave}], visited = [${visited}]")
            if (nextCave is EndCave) {
//                println("arrived at end")
                val withEnd = visited.toMutableList() + nextCave
                completedPaths += withEnd
                return withEnd
            } else if (nextCave is SmallCave && nextCave in visited) {
//                println("already been to this small cave: $nextCave")
                return visited
            }
            return nextCave.connections.flatMap {
//                println("in $nextCave and going to $it")
                loop(it, visited.toMutableList() + nextCave)
            }
        }

        loop(this, listOf())
//        println("completedPaths = ${completedPaths.size} / ${completedPaths}")
        return completedPaths.size
    }

    fun visit2(): Int {
        val completedPaths = mutableSetOf<List<Cave>>()
        fun loop(nextCave: Cave, visited: List<Cave>): List<Cave> {
//            println("nextCave = [${nextCave}], visited = [${visited}]")
            if (nextCave is EndCave) {
//                println("arrived at end")
                val withEnd = visited.toMutableList() + nextCave
                completedPaths += withEnd
                return withEnd
            } else if (nextCave is SmallCave) {
                // exhausted our visits to a Small Cave?
                val smallCavesVisitedTwice =
                    visited.filterIsInstance<SmallCave>().groupBy { it }.filter { (_, v) -> v.size > 1 }
                if (smallCavesVisitedTwice.isNotEmpty()) {
//                    println("smallCavesVisitedTwice = ${smallCavesVisitedTwice}")
                    if (nextCave in smallCavesVisitedTwice) {
//                        println("already been to this small cave twice: $nextCave")
                        return visited
                    } else if (nextCave in visited) {
//                        println("have been to this small cave though: $nextCave")
                        return visited
                    }
                }
            }
            return nextCave.connections.flatMap {
//                println("in $nextCave and going to $it")
                loop(it, visited.toMutableList() + nextCave)
            }
        }

        loop(this, listOf())
//        println("completedPaths = ${completedPaths.size} / ${completedPaths}")
        return completedPaths.size
    }
}

data class SmallCave(val name: String) : Cave(name)
data class LargeCave(val name: String) : Cave(name)
object StartCave : Cave("start")
object EndCave : Cave("end")