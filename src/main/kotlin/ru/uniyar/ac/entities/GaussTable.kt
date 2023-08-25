package ru.uniyar.ac.entities

class GaussTable {
    var swappedColumns: List<Int> = emptyList()
    var matrix: MutableList<MutableList<Double>> = mutableListOf()
    var type: GaussType? = null

    /*override fun toString(): String {
        val matrLine = StringBuilder()
        for (fractions in matr) {
            for (fraction in fractions) {
                matrLine.append("\t\t").append(fraction).append(" ")
            }
            matrLine.append("\n")
        }
        return "GaussTable{" +
                "\n\tswapedCols=" + swapedCols +
                ",\n\tmatr=[\n" + matrLine +
                "\t],\n\ttype=" + type +
                "\n}"
    }*/
}
