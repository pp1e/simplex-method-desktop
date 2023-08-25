package ru.uniyar.ac.ui.navigation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.animation.child.childAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.animation.child.slide
import ru.uniyar.ac.ui.screens.ArtificialBasisScreen
import ru.uniyar.ac.ui.screens.GraphicScreen
import ru.uniyar.ac.ui.screens.MainScreen
import ru.uniyar.ac.ui.screens.SimplexScreen

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun Content(router: MainRouter) {
    Children(
        routerState = router.routerState,
        animation = childAnimation(slide()),
    ) {
        when (val child = it.instance) {
            is MainRouter.Child.Main -> MainScreen(child.component)
            is MainRouter.Child.ArtificialBasis -> ArtificialBasisScreen(child.component)
            is MainRouter.Child.Graphic -> GraphicScreen()
            is MainRouter.Child.Simplex -> SimplexScreen(child.component)
        }
    }
}
