package ru.uniyar.ac.ui.elements

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import ru.uniyar.ac.ui.constants.UiConstants

@Composable
fun HeaderText(text: String) {
    Text(
        text = text,
        fontSize = UiConstants.HeaderTextSize
    )
}
