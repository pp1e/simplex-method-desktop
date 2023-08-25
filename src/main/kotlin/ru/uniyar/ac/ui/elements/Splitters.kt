package ru.uniyar.ac.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.uniyar.ac.ui.constants.UiConstants
import ru.uniyar.ac.ui.constants.colors

@Composable
fun VerticalSplitter() {
    Spacer(
        modifier = androidx.compose.ui.Modifier
            .padding(start = UiConstants.Padding, end = UiConstants.Padding)
            .fillMaxHeight()
            .width(1.dp)
            .background(colors.primary)
    )
}

@Composable
fun HorizontalSplitter() {
    Spacer(
        modifier = androidx.compose.ui.Modifier
            .padding(bottom = UiConstants.Padding, top = UiConstants.Padding)
            .fillMaxWidth()
            .height(1.dp)
            .background(colors.primary)
    )
}

@Composable
fun HorizontalSplitter(width: Dp) {
    Spacer(
        modifier = androidx.compose.ui.Modifier
            .padding(bottom = UiConstants.Padding, top = UiConstants.Padding)
            .width(width)
            .height(1.dp)
            .background(colors.primary)
    )
}

@Composable
fun VerticalSplitter(height: Dp) {
    Spacer(
        modifier = androidx.compose.ui.Modifier
            .padding(start = UiConstants.Padding, end = UiConstants.Padding)
            .height(height)
            .width(1.dp)
            .background(colors.primary)
    )
}
