package ru.uniyar.ac.ui.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.RouterState
import com.arkivanov.decompose.router.pop
import com.arkivanov.decompose.router.popWhile
import com.arkivanov.decompose.router.push
import com.arkivanov.decompose.router.router
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.badoo.reaktive.base.Consumer
import ru.uniyar.ac.calculations.ArtificialBasisSolver
import ru.uniyar.ac.calculations.SimplexSolver
import ru.uniyar.ac.components.artificial.ArtificialBasisComponent
import ru.uniyar.ac.components.main.MainComponent
import ru.uniyar.ac.components.simplex.SimplexComponent
import ru.uniyar.ac.entities.FractionType
import ru.uniyar.ac.entities.TaskType
import ru.uniyar.ac.utils.Consumer

class MainRouter internal constructor(
    private val componentContext: ComponentContext,
    private val mainComponent: (ComponentContext, Consumer<MainComponent.Output>) -> MainComponent,
    private val artificialBasisComponent: (
        ComponentContext,
        Consumer<ArtificialBasisComponent.Output>,
        solver: ArtificialBasisSolver,
        fractionType: FractionType,
        taskType: TaskType
    ) -> ArtificialBasisComponent,
    private val simplexComponent: (
        ComponentContext,
        Consumer<SimplexComponent.Output>,
        solver: SimplexSolver,
        fractionType: FractionType,
        taskType: TaskType
    ) -> SimplexComponent
) : ComponentContext by componentContext {

    private val router = router<ScreenConfig, Child>(
        initialConfiguration = ScreenConfig.Main,
        childFactory = ::createChild,
    )

    val routerState: Value<RouterState<*, Child>> = router.state

    constructor(
        componentContext: ComponentContext,
        storeFactory: StoreFactory,
    ) : this(
        componentContext = componentContext,
        mainComponent = { component, output ->
            MainComponent(
                componentContext = component,
                storeFactory = storeFactory,
                output = output,
            )
        },
        artificialBasisComponent = { component, output, solver, fracType, taskType ->
            ArtificialBasisComponent(
                componentContext = component,
                storeFactory = storeFactory,
                output = output,
                solver = solver,
                fractionType = fracType,
                taskType = taskType
            )
        },
        simplexComponent = { component, output, solver, fracType, taskType ->
            SimplexComponent(
                componentContext = component,
                storeFactory = storeFactory,
                output = output,
                solver = solver,
                fractionType = fracType,
                taskType = taskType
            )
        }
    )

    private fun createChild(
        screenConfig: ScreenConfig,
        componentContext: ComponentContext
    ): Child =
        when (screenConfig) {
            is ScreenConfig.Main -> Child.Main(
                mainComponent(
                    componentContext,
                    Consumer(::onMainOutput)
                )
            )

            is ScreenConfig.Simplex -> Child.Simplex(
                simplexComponent(
                    componentContext,
                    Consumer(::onSimplexOutput),
                    screenConfig.solver,
                    screenConfig.fractionType,
                    screenConfig.taskType
                )
            )

            is ScreenConfig.ArtificialBasis -> Child.ArtificialBasis(
                artificialBasisComponent(
                    componentContext,
                    Consumer(::onArtificialBasisOutput),
                    screenConfig.solver,
                    screenConfig.fractionType,
                    screenConfig.taskType
                )

            )

            is ScreenConfig.Graphic -> Child.Graphic(
                -1
            )
        }

    private fun onMainOutput(output: MainComponent.Output): Unit =
        when (output) {
            is MainComponent.Output.SimplexTransit -> router.push(ScreenConfig.Simplex(output.solver, output.fractionType, output.taskType))
            is MainComponent.Output.GraphicTransit -> router.push(ScreenConfig.Graphic)
            is MainComponent.Output.ArtificialBasisTransit -> router.push(ScreenConfig.ArtificialBasis(output.solver, output.fractionType, output.taskType))
        }

    private fun onArtificialBasisOutput(output: ArtificialBasisComponent.Output): Unit =
        when (output) {
            is ArtificialBasisComponent.Output.NextStepTransit -> router.push(ScreenConfig.ArtificialBasis(output.solver, output.fractionType, output.taskType))
            is ArtificialBasisComponent.Output.MainTransit -> router.popWhile { it != ScreenConfig.Main }
            is ArtificialBasisComponent.Output.PrevStepTransit -> router.pop()
            is ArtificialBasisComponent.Output.SimplexTransit -> router.push(ScreenConfig.Simplex(output.solver, output.fractionType, output.taskType))
        }

    private fun onSimplexOutput(output: SimplexComponent.Output): Unit =
        when (output) {
            is SimplexComponent.Output.NextStepTransit -> router.push(ScreenConfig.Simplex(output.solver, output.fractionType, output.taskType))
            is SimplexComponent.Output.MainTransit -> router.popWhile { it != ScreenConfig.Main }
            is SimplexComponent.Output.PrevStepTransit -> router.pop()
        }

    sealed class Child {
        data class Main(val component: MainComponent) : Child()
        data class Simplex(val component: SimplexComponent) : Child()
        data class ArtificialBasis(val component: ArtificialBasisComponent) : Child()
        data class Graphic(val component: Int) : Child()
    }

    private sealed class ScreenConfig : Parcelable {
        @Parcelize
        object Main : ScreenConfig()

        @Parcelize
        data class Simplex(val solver: SimplexSolver, val fractionType: FractionType, val taskType: TaskType) : ScreenConfig()

        @Parcelize
        data class ArtificialBasis(val solver: ArtificialBasisSolver, val fractionType: FractionType, val taskType: TaskType) : ScreenConfig()

        @Parcelize
        object Graphic : ScreenConfig()
    }
}
