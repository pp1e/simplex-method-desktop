package ru.uniyar.ac.utils

fun <T> List<T>.insert(index: Int, element: T): List<T> {
    val result = this.toMutableList()
    result[index] = element
    return result.toList()
}

fun <T> List<List<T>>.insert(row: Int, column: Int, element: T): List<List<T>> {
    val result = this.toMutableList()
    result[row] = result[row].insert(column, element)
    return result.toList()
}
