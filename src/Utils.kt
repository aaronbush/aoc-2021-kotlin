import java.io.File

fun readInput(name: String) = File("src/data", "$name.txt").readLines()