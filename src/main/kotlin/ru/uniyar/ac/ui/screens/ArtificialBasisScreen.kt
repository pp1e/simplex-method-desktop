package ru.uniyar.ac.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import ru.uniyar.ac.components.artificial.ArtificialBasisComponent
import ru.uniyar.ac.entities.FractionType
import ru.uniyar.ac.ui.constants.UiConstants
import ru.uniyar.ac.ui.elements.HeaderText
import ru.uniyar.ac.ui.elements.SimplexAnswer
import ru.uniyar.ac.ui.elements.SimplexTable
import ru.uniyar.ac.ui.elements.SwitchWithLabel
import ru.uniyar.ac.ui.elements.VerticalSplitter

@Composable
fun ArtificialBasisScreen(component: ArtificialBasisComponent) {
    val model by component.models.subscribeAsState()

    Column {
        SwitchWithLabel(
            label = "Обыкновенные дроби",
            checked = model.fractionType == FractionType.COMMON,
            onSwitchToggled = {
                if (model.fractionType == FractionType.COMMON) component.onFractionTypeChanged(FractionType.DECIMAL)
                else component.onFractionTypeChanged(FractionType.COMMON)
            }
        )

        Row {
            SimplexTable(
                values = model.simplexTable,
                header = "Симплекс-таблица вспомогательной задачи, шаг ${model.iteration}",
                rowNames = model.rowNames,
                columnNames = model.columnNames,
                onPivotSelected = component::onPivotSelected,
                iteration = model.iteration,
            )

            if (model.noSolution) {
                VerticalSplitter(130.dp)
                HeaderText("Нет решения.")
            }

            if (model.artificialAnswer.isNotEmpty()) {
                VerticalSplitter(130.dp)
                SimplexAnswer(
                    answer = model.artificialAnswer,
                    header = "Ответ"
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Column {
                Button(
                    onClick = component::onPrevStepClicked,
                    modifier = Modifier
                        .padding(UiConstants.Padding),
                ) {
                    Text("Шаг назад")
                }

                Button(
                    onClick = component::onBackToMainScreenClicked,
                    modifier = Modifier
                        .padding(UiConstants.Padding),
                ) {
                    Text("Главный экран")
                }
            }

            Column {
                Button(
                    onClick = component::onNextStepClicked,
                    modifier = Modifier
                        .padding(UiConstants.Padding),
                ) {
                    Text("Шаг вперёд")
                }

                /*Button(
                    onClick = {},
                    modifier = Modifier
                        .padding(UiConstants.Padding),
                ) {
                    Text("Получить ответ")
                }*/
            }
        }
    }
}
