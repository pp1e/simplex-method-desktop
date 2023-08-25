package ru.uniyar.ac.ui.elements

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.uniyar.ac.ui.constants.UiConstants
import ru.uniyar.ac.ui.constants.colors

@Composable
fun InputTableItem(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean
) {
    Card(
        modifier = Modifier
            .padding(UiConstants.GridPadding)
            .width(UiConstants.InputTableItemWidth)
            .height(UiConstants.InputTableItemHeight),
        backgroundColor = if (isError) colors.error else colors.primary
    ) {
        BasicTextField(
            modifier = Modifier
                .padding(start = UiConstants.TextPadding, end = UiConstants.TextPadding),
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
        )
    }
}
