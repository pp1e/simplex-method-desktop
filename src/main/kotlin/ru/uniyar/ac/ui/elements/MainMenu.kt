package ru.uniyar.ac.ui.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import ru.uniyar.ac.ui.constants.UiConstants
import kotlin.system.exitProcess

@Composable
fun MainMenu(
    onSaveClicked: () -> Unit,
    onLoadClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(top = UiConstants.Padding)
            .height(UiConstants.TopPanelHeight)
            .fillMaxWidth()
    ) {
        MenuButton(
            text = "Сохранить",
            onClicked = onSaveClicked,
            icon = Icons.Default.Save,
        )
        MenuButton(
            text = "Загрузить",
            onClicked = onLoadClicked,
            icon = Icons.Default.Download,
        )
        MenuButton(
            text = "Выход",
            onClicked = { exitProcess(0) },
            icon = Icons.Default.ExitToApp,
        )
    }
}

@Composable
fun MenuButton(
    text: String,
    onClicked: () -> Unit,
    icon: ImageVector,
    enabled: Boolean = true,
    focusRequester: FocusRequester = FocusRequester(),
) {
    Button(
        modifier = Modifier
            .height(UiConstants.MenuHeight)
            .padding(horizontal = UiConstants.Padding)
            .focusRequester(focusRequester),
        onClick = onClicked,
        enabled = enabled,
    ) {
        Text(
            text = text,
            fontSize = UiConstants.MenuTextSize
        )
        Icon(
            imageVector = icon,
            contentDescription = ""
        )
    }
}
