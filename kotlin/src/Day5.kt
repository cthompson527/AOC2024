import java.io.File

typealias Rules =
        Map<Int, MutableSet<Int>>
typealias Update = MutableList<Int>
typealias Updates = List<Update>
typealias Manual = Pair<Rules, Updates>

fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val tmp = this[index1]
    this[index1] = this[index2]
    this[index2] = tmp
}

class Day5 {
    private fun readFile(filename: String): Manual {
        val manual = Pair(mutableListOf<String>(), mutableListOf<String>())
        var currentList = manual.first
        File(filename).useLines { lines ->
            lines.forEach { line ->
                if (line.isNotEmpty()) {
                    currentList.add(line)
                } else {
                    currentList = manual.second
                }
            }
        }

        val (rules, updates) = manual

        return Manual(parseRules(rules), parseUpdates(updates))
    }

    private fun parseRules(rules: List<String>): Rules {
        return rules
            .map { it.split("|") }
            .fold(mutableMapOf<Int, MutableSet<Int>>()) { map, vals ->
                map.getOrPut(vals[0].toInt(), { mutableSetOf() }).add(vals[1].toInt())
                map
            }
    }

    private fun parseUpdates(updates: List<String>): Updates {
        return updates
            .map { it.split(",") }
            .map { update -> update.map { it.toInt() }.toMutableList() }
    }

    private fun checkUpdate(rules: Rules, update: Update): Boolean {
        return update.windowed(size = 2, step = 1).all { rules.getOrDefault(it[0], mutableSetOf()).contains(it[1]) }
    }

    private fun findMiddle(update: Update): Int {
        return update[update.size / 2]
    }

    private fun reorder(rules: Rules, update: Update): Update {
        val mistakes = update
            .mapIndexed { index, it -> Pair(index, it) }
            .windowed(size = 2, step = 1)
            .filterNot { window -> rules.getOrDefault(window[0].second, mutableSetOf()).contains(window[1].second) }

        if (mistakes.isNotEmpty()) {
            mistakes.forEach { window -> update.swap(window[0].first, window[1].first) }
            return reorder(rules, update)
        } else {
            return update
        }

    }

    fun partOne(filename: String): Int {
        val (rules, updates) = readFile(filename)
        return updates
            .filter { checkUpdate(rules, it) }
            .sumOf { findMiddle(it) }
    }

    fun partTwo(filename: String): Int {
        val (rules, updates) = readFile(filename)
        return updates
            .filterNot { checkUpdate(rules, it) }
            .map { reorder(rules, it) }
            .sumOf { findMiddle(it) }
    }
}

fun main() {
    val partOne = Day5().partOne("inputs/day5_sample.txt")
    println("PartOne: $partOne")
    val partTwo = Day5().partTwo("inputs/day5.txt")
    println("PartTwo: $partTwo")
}