package ru.uniyar.ac.ui.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.uniyar.ac.ui.constants.UiConstants

@Composable
fun SwitchWithLabel(
    label: String,
    checked: Boolean,
    onSwitchToggled: () -> Unit,
    enabled: Boolean = true,
) {
    Row {
        Text(
            text = label,
            modifier = Modifier.align(Alignment.CenterVertically)
                .padding(UiConstants.Padding)
        )
        Switch(
            checked = checked,
            onCheckedChange = { onSwitchToggled() },
            enabled = enabled,
        )
    }
}
