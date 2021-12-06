package day4

import readInput

fun main() {
    val input = readInput("Day04")
    println("part1: ${part1(input)}")
    println("part2: ${part2(input)}")
}

fun part1(input: List<String>): Int {
    // 3d array of 'boards' is one way to design - // todo: need to learn APL
    val plays = parsePlays(input[0])
    val boards = setupBoards(input.drop(2))

    for (num in plays) {
        boards.forEach {
            it play num
            if (it.winner) {
                println("BINGO on $it")
                return it.unmarked().sum() * num
            }
        }
    }
    throw IllegalStateException("oops")
}

fun part2(input: List<String>): Int {
    val plays = parsePlays(input[0])
    val boards = setupBoards(input.drop(2))

    var winners = 0

    for (num in plays) {
        boards.filterNot { it.winner }.forEach {
            it play num
            if (it.winner) {
                winners += 1
                if (boards.size == winners) {
                    println("with $num last winner found: $it")
                    return it.unmarked().sum() * num
                }
            }
        }
    }
    throw IllegalStateException("oops")
}

fun parsePlays(input: String) = input.split(",").map { it.trim().toInt() }

fun setupBoards(input: List<String>): List<Board> {
    val boardInput = input.filterNot { it.trim().isEmpty() }.map {
        it.trim().split("\\s+".toRegex()).map { n -> n.toInt() }
    }
    val rawBoards = chunkBoards(boardInput)
    return rawBoards.map { Board.from(it) }
}

fun chunkBoards(input: List<List<Int>>): List<List<List<Int>>> {
    return input.windowed(5, 5)
}

data class Board(val cells: List<Cell>, var winner: Boolean = false) {

    companion object {
        fun from(input: List<List<Int>>): Board {
            val cells = input.flatMapIndexed { rowNum, rowValues ->
                rowValues.mapIndexed { colNum, value ->
                    Cell(rowNum to colNum, value)
                }
            }
            return Board(cells)
        }
    }

    data class Cell(val location: Pair<Int, Int>, val value: Int, var played: Boolean = false)

    infix fun play(num: Int) {
        cells.filter { cell -> cell.value == num }.forEach { cell -> cell.played = true }
        winner = bingo()
    }

    private fun bingo(): Boolean {
        // check rows & cols
        for (i in 0 until 5) {
            val allPlayed = cells.filter { cell -> cell.location.first == i }
                .all { cell -> cell.played } or
                    cells.filter { cell -> cell.location.second == i }
                        .all { cell -> cell.played }
            if (allPlayed) {
                return true
            }
        }
        return false
    }

    fun unmarked() = cells.filterNot { it.played }.map { it.value }
}