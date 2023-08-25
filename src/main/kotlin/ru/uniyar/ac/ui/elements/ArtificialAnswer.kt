package ru.uniyar.ac.ui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.uniyar.ac.ui.constants.UiConstants

@Composable
fun SimplexAnswer(
    answer: List<String>,
    header: String
) {
    Column(modifier = Modifier.padding(UiConstants.Padding)) {
        HeaderText(header)
        Text("F = ${answer.first()}")
        Text(parseAnswer(answer))
    }
}

fun parseAnswer(answer: List<String>) =
    answer.drop(1).joinToString(prefix = "(", separator = ", ", postfix = ")")
