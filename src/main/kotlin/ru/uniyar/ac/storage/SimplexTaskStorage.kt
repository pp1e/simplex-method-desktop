package ru.uniyar.ac.storage

import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileReader

val gson = GsonBuilder()
    .setPrettyPrinting()
    .create()

class SimplexData(val restriction: List<List<Pair<Boolean, String>>>, val function: List<Pair<Boolean, String>>)

fun saveInFile(restriction: List<List<Pair<Boolean, String>>>, function: List<Pair<Boolean, String>>, file: String) {
    val gsonObj = gson.toJson(SimplexData(restriction, function))

    val f = File(file)
    f.writeText(gsonObj)
}

fun readFromFile(file: String): SimplexData {
    val res = gson.fromJson(
        FileReader(file),
        SimplexData::class.java
    )
    println(res.restriction)
    println(res.function)
    return res
}
