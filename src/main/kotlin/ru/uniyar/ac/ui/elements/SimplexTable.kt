package ru.uniyar.ac.ui.elements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import ru.uniyar.ac.entities.PivotData
import ru.uniyar.ac.ui.constants.UiConstants
import ru.uniyar.ac.ui.constants.colors
import ru.uniyar.ac.utils.transpose
import kotlin.reflect.KFunction1

@Composable
fun SimplexTable(
    values: List<List<Pair<String, PivotData>>>,
    header: String,
    rowNames: List<String>,
    columnNames: List<String>,
    onPivotSelected: KFunction1<Pair<Int, Int>, Unit>,
    iteration: Int = 0
) {
    val valuesIterator = transpose(values).iterator()
    Box(
        modifier = Modifier
            .padding(UiConstants.Padding)
    ) {
        Column {
            HeaderText(header)

            Row {
                Column {
                    TableHeader("X^$iteration")

                    for (rowName in rowNames)
                        TableHeader(rowName)
                }

                for (columnName in columnNames) {
                    Column {
                        TableHeader(columnName)
                        if (valuesIterator.hasNext())
                            for (value in valuesIterator.next())
                                if (value.second.row == -1)
                                    SimplexTableItem(
                                        value = value.first,
                                    )
                                else
                                    SimplexTablePivot(
                                        value = value.first,
                                        isSelected = value.second.isSelected
                                    ) { onPivotSelected(Pair(value.second.row, value.second.column)) }
                    }
                }
            }
        }
    }
}

@Composable
fun SimplexTablePivot(
    value: String,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val hovered = interaction.collectIsHoveredAsState()
    val color = if (hovered.value) colors.secondaryVariant else if (isSelected) colors.primaryVariant else colors.primary

    Card(
        modifier = Modifier
            .padding(UiConstants.GridPadding)
            .height(UiConstants.InputTableItemHeight)
            .clickable { onSelected() }
            .hoverable(
                interactionSource = interaction,
                enabled = true
            ),
        backgroundColor = color,
        border = BorderStroke(color = colors.secondary, width = UiConstants.BorderSize)
    ) {
        Text(
            modifier = Modifier
                .padding(start = UiConstants.TextPadding, end = UiConstants.TextPadding),
            text = value,
        )
    }
}

@Composable
fun SimplexTableItem(value: String) {
    Card(
        modifier = Modifier
            .padding(UiConstants.GridPadding)
            .height(UiConstants.InputTableItemHeight),
        backgroundColor = colors.primary
    ) {
        Text(
            modifier = Modifier
                .padding(start = UiConstants.TextPadding, end = UiConstants.TextPadding),
            text = value,
        )
    }
}
