package day2

object Location {
    var depth = 0
    var fwd = 0
    var aim = 0
}

fun init() {
    Location.depth = 0
    Location.fwd = 0
    Location.aim = 0
}

class SUB {
    val go = TrackMotion()

    val report: Unit
        get() {
            println("distance: ${Location.fwd}")
            println("depth: ${Location.depth}")
            println("aim: ${Location.aim}")
        }
    
    inner class TrackMotion {
        infix fun forward(n: Int) {
            Location.fwd += n
        }

        infix fun down(n: Int) {
            Location.depth += n
        }

        infix fun up(n: Int) {
            Location.depth -= n
        }
    }
}

class SUB2 {
    val go = TrackMotion()

    val report: Unit
        get() {
            println("distance: ${Location.fwd}")
            println("depth: ${Location.depth}")
            println("aim: ${Location.aim}")
        }

    inner class TrackMotion {
        infix fun forward(n: Int) {
            Location.fwd += n
            Location.depth += Location.aim * n
        }

        infix fun down(n: Int) {
            Location.aim += n
        }

        infix fun up(n: Int) {
            Location.aim -= n
        }
    }
}

fun sub(fn: SUB.() -> Unit) {
    init()
    val sub = SUB()
    sub.fn()
}

fun sub2(fn: SUB2.() -> Unit) {
    init()
    val sub = SUB2()
    sub.fn()
}

fun part1() {
    sub {
        go forward 5
        go down 5
        go forward 8
        go up 3
        go down 8
        go forward 2
        report
    }
}

fun part2() {
    sub2 {
        go forward 5
        go down 5
        go forward 8
        go up 3
        go down 8
        go forward 2
        report
    }
}

fun main() {
    part1()
    part2()
}