package ru.uniyar.ac.ui.elements

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.uniyar.ac.ui.constants.UiConstants

@Composable
fun InputTable(
    values: List<List<Pair<Boolean, String>>>,
    header: String,
    onValueChange: (Int, Int, String) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(UiConstants.Padding)
    ) {
        Column {
            HeaderText(header)
            HorizontalSplitter((UiConstants.InputTableItemWidth + UiConstants.GridPadding) * (values.first().size + 1))

            Row {
                for (index in 1..values.first().size)
                    TableHeader(if (index == values.first().size) "C" else "X" + index.toString())
            }

            for (rowIndex in values.indices)
                Row {
                    for (columnIndex in values[rowIndex].indices) {
                        InputTableItem(
                            value = values[rowIndex][columnIndex].second,
                            onValueChange = { onValueChange(rowIndex, columnIndex, it) },
                            isError = values[rowIndex][columnIndex].first
                        )
                    }
                }
        }
    }
}
