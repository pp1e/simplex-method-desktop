package ru.uniyar.ac.ui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import ru.uniyar.ac.ui.constants.UiConstants
import ru.uniyar.ac.ui.constants.colors

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun Spinner(
    value: String,
    onValueChanged: (String) -> Unit,
    isError: Boolean = false,
    label: String,
    placeholder: String = "",
    maxValue: Int = Int.MAX_VALUE,
    minValue: Int = Int.MIN_VALUE,
    enabled: Boolean = true,
) {
    Row(modifier = Modifier.padding(UiConstants.Padding)) {
        TextField(
            modifier = Modifier
                .height(UiConstants.SpinnerHeight)
                .width(UiConstants.SpinnerWidth)
                .onKeyEvent {
                    when (it.key) {
                        Key.DirectionDown -> {
                            decreaseValue(value.trim(), onValueChanged, minValue)
                            true
                        }
                        Key.DirectionUp -> {
                            increaseValue(value.trim(), onValueChanged, maxValue)
                            true
                        }
                        else -> false
                    }
                },
            value = value,
            onValueChange = {
                val newValue = it.trim()
                if (newValue.isBlank() or (newValue == "-"))
                    onValueChanged(newValue)
                try {
                    if ((newValue.toInt() <= maxValue) && (newValue.toInt() >= minValue))
                        onValueChanged(newValue)
                    else if (newValue.toInt() > maxValue)
                        onValueChanged(maxValue.toString())
                    else if (newValue.toInt() < minValue) onValueChanged(minValue.toString())
                } catch (_: Exception) { }
            },
            isError = isError,
            enabled = enabled,
            label = { Text(text = label) },
            placeholder = { Text(text = placeholder) },
            colors = TextFieldDefaults.textFieldColors(
                focusedLabelColor = colors.secondary,

            )

        )
        Column {
            Button(
                modifier = Modifier
                    .height(UiConstants.SpinnerHeight / 2)
                    .width(UiConstants.SpinnerWidth / 2)
                    .focusProperties { canFocus = false },
                onClick = { increaseValue(value.trim(), onValueChanged, maxValue) },
                enabled = enabled,
            ) {
                Icon(
                    Icons.Default.KeyboardArrowUp,
                    contentDescription = "Увеличить значение"
                )
            }
            Button(
                modifier = Modifier
                    .height(UiConstants.SpinnerHeight / 2)
                    .width(UiConstants.SpinnerWidth / 2)
                    .focusProperties { canFocus = false },
                onClick = { decreaseValue(value.trim(), onValueChanged, minValue) },
                enabled = enabled,
            ) {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "Уменьшить значение"
                )
            }
        }
    }
}

fun increaseValue(value: String, onValueChanged: (String) -> Unit, maxValue: Int) {
    try {
        if (value.toInt() != maxValue)
            onValueChanged((value.toInt() + 1).toString())
    } catch (e: NumberFormatException) {
        onValueChanged(maxValue.toString())
    }
}

fun decreaseValue(value: String, onValueChanged: (String) -> Unit, minValue: Int) {
    try {
        if (value.toInt() != minValue)
            onValueChanged((value.toInt() - 1).toString())
    } catch (e: NumberFormatException) {
        onValueChanged(minValue.toString())
    }
}
