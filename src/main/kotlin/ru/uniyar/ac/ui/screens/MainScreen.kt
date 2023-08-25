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
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import ru.uniyar.ac.components.main.MainComponent
import ru.uniyar.ac.entities.FractionType
import ru.uniyar.ac.entities.SolutionMethod
import ru.uniyar.ac.entities.TaskType
import ru.uniyar.ac.ui.constants.UiConstants
import ru.uniyar.ac.ui.elements.InputTable
import ru.uniyar.ac.ui.elements.MainMenu
import ru.uniyar.ac.ui.elements.RadioChooser
import ru.uniyar.ac.ui.elements.Spinner
import ru.uniyar.ac.ui.elements.VerticalSplitter
import ru.uniyar.ac.utils.TxtFileFilter
import javax.swing.JFileChooser

@Composable
fun MainScreen(component: MainComponent) {
    val model by component.models.subscribeAsState()

    Column {
        MainMenu(
            onSaveClicked = {
                val f = JFileChooser()
                f.fileFilter = TxtFileFilter()
                val result = f.showSaveDialog(null)
                if (result == JFileChooser.APPROVE_OPTION)
                    component.saveSimplexData(f.selectedFile.absolutePath)
            },
            onLoadClicked = {
                val f = JFileChooser()
                f.fileFilter = TxtFileFilter()
                val result = f.showSaveDialog(null)
                if (result == JFileChooser.APPROVE_OPTION)
                    component.loadSimplexData(f.selectedFile.absolutePath)
            }
        )

        Row {
            Column {
                Spinner(
                    value = model.restrictionCount.toString(),
                    onValueChanged = component::onRestrictionCountChanged,
                    label = "Число ограничений",
                    maxValue = 16,
                    minValue = 1
                )

                Spinner(
                    value = model.variablesCount.toString(),
                    onValueChanged = component::onVariablesCountChanged,
                    label = "Число переменных",
                    maxValue = 16,
                    minValue = 1
                )

                RadioChooser(
                    currentSelection = model.fractionType,
                    selections = mapOf(
                        "Обыкновенные" to FractionType.COMMON,
                        "Десятичные" to FractionType.DECIMAL
                    ),
                    onSelectionChanged = component::onFractionTypeChanged,
                    label = "Тип дробей"
                )

                RadioChooser(
                    currentSelection = model.solutionMethod,
                    selections = mapOf(
                        "Симлекс" to SolutionMethod.SIMPLEX,
                        "Искусственного базиса" to SolutionMethod.ARTIFICIAL_BASIS,
                        "Графический" to SolutionMethod.GRAPHIC
                    ),
                    onSelectionChanged = component::onSolutionMethodChanged,
                    label = "Метод решения"
                )

                RadioChooser(
                    currentSelection = model.taskType,
                    selections = mapOf(
                        "Минимум" to TaskType.MIN,
                        "Максимум" to TaskType.MAX
                    ),
                    onSelectionChanged = component::onTaskTypeChanged,
                    label = "Тип задачи"
                )
            }

            VerticalSplitter()

            Column {
                InputTable(
                    header = "Целевая функция: ",
                    values = listOf(model.function),
                    onValueChange = { _, column, value -> component.onFunctionElementChanged(column, value) }
                )

                InputTable(
                    header = "Ограничения задачи:",
                    values = model.restrictions,
                    onValueChange = component::onRestrictionElementChanged
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Button(
                        onClick = component::onContinueClicked,
                        modifier = Modifier
                            .padding(UiConstants.Padding),
                        enabled = model.isDataCorrect
                    ) {
                        Text("Продолжить")
                    }
                }
            }
        }
    }
}
