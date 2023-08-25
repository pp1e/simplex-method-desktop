package ru.uniyar.ac.ui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import ru.uniyar.ac.ui.constants.UiConstants
import ru.uniyar.ac.ui.constants.colors

@Composable
fun <T : Enum<T>> RadioChooser(
    currentSelection: T,
    selections: Map<String, T>,
    onSelectionChanged: (T) -> Unit,
    label: String = "",
    enabled: Boolean = true,
) {
    Row(Modifier.selectableGroup()) {
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(UiConstants.Padding)
        )

        Column {
            for ((methodName, method) in selections) {
                Row {
                    RadioButton(
                        selected = (currentSelection == method),
                        onClick = { onSelectionChanged(method) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = colors.primaryVariant,
                            unselectedColor = colors.primary,
                            disabledColor = Color.LightGray
                        ),
                        enabled = enabled,
                    )
                    Text(
                        text = methodName,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}
