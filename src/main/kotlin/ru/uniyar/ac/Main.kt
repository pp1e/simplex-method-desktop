import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import ru.uniyar.ac.ui.constants.colors
import ru.uniyar.ac.ui.navigation.Content
import ru.uniyar.ac.ui.navigation.MainRouter

@OptIn(ExperimentalDecomposeApi::class)
fun main() = application {
    val lifecycle = LifecycleRegistry()
    val windowState = rememberWindowState(size = DpSize(width = 1050.dp, height = 900.dp))
    LifecycleController(lifecycle, windowState)

    val root = MainRouter(
        componentContext = DefaultComponentContext(lifecycle = lifecycle),
        storeFactory = DefaultStoreFactory())

    Window(
        onCloseRequest = ::exitApplication,
        title = "Решение задач симплекс-методом",
        state = windowState,
    ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            MaterialTheme(colors = colors) {
                Content(root)
            }
        }
    }
}
