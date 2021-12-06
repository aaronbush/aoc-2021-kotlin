package day6

import readInput

fun main() {
    val input = readInput("Day06")
    val buckets = loadBuckets(input[0])
    println(part1(buckets, 256))
}

fun part1(buckets: LongArray, numDays: Int): Long {
    var nextDay = buckets
    for (day in 1..numDays) {
        nextDay = rotateBuckets(nextDay)
//        printBuckets(day, nextDay)
    }
    return nextDay.sum()
}

fun loadBuckets(input: String): LongArray {
    val buckets = LongArray(9) { 0 }
    val nums = input.split(",").map { it.toInt() }.groupBy { it }
    nums.forEach { (day, each) -> buckets[day] = each.size.toLong() }
    return buckets
}

fun rotateBuckets(buckets: LongArray): LongArray {
    // shift from 0 to 6
    // the number of 0's being shifted 'off' to 6 is new number of 8's
    val result = LongArray(9) { 0 }
    for (n in 8 downTo 0) {
        if (n == 0) {
            result[6] = result[6] + buckets[n]
            result[8] = buckets[n]
        } else {
            result[n - 1] = buckets[n]
        }
    }
    return result
}

fun printBuckets(dayNum: Int, buckets: IntArray) {
    println()
    print("Day $dayNum: ")
    buckets.forEachIndexed { i, b ->
        print("[${i}] = $b   ")
    }
}