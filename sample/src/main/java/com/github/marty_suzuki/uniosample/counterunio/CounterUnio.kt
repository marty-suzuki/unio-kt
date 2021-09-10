package com.github.marty_suzuki.uniosample.counterunio

import com.github.marty_suzuki.unio.Dependency
import com.github.marty_suzuki.unio.OutputFactory
import com.github.marty_suzuki.unio.Unio
import com.github.marty_suzuki.unio.UnioFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

@ExperimentalCoroutinesApi
class CounterUnio @AssistedInject constructor(
    input: CounterUnioInput,
    state: State,
    @Assisted viewModelScope: CoroutineScope,
    @Assisted onCleared: Flow<Unit>,
) : Unio<
        CounterUnioInput,
        CounterUnioOutput,
        CounterUnio.Extra,
        CounterUnio.State
        >(
    input = input,
    extra = Extra(
        startValue = 5,
        onCleared = onCleared
    ),
    state = state,
    outputFactory = CounterUnio,
    viewModelScope = viewModelScope
) {

    @AssistedFactory
    interface Factory: UnioFactory<CounterUnioInput, CounterUnioOutput> {
        override fun create(
            @Assisted viewModelScope: CoroutineScope,
            @Assisted onCleared: Flow<Unit>,
        ): CounterUnio
    }

    class State : Unio.State {
        val count = MutableStateFlow(0)
        val isCountDownEnabled = MutableStateFlow(false)
    }

    class Extra(
        val startValue: Int,
        val onCleared: Flow<Unit>,
    ) : Unio.Extra

    companion object : OutputFactory<
            CounterUnioInput,
            CounterUnioOutput,
            Extra,
            State
            > {
        override fun create(
            dependency: Dependency<CounterUnioInput, Extra, State>,
            viewModelScope: CoroutineScope
        ): CounterUnioOutput {
            val state = dependency.state
            val extra = dependency.extra

            listOf(
                dependency.getFlow(CounterUnioInput::countUp).map { 1 },
                dependency.getFlow(CounterUnioInput::countDown).map { -1 }
            )
                .merge()
                .map { state.count.value + it }
                .onStart { emit(extra.startValue) }
                .onEach {
                    state.count.emit(it)
                    state.isCountDownEnabled.emit(it > 0)
                }
                .launchIn(viewModelScope)

            return CounterUnioOutput(
                count = state.count.map { it.toString() },
                isCountDownEnabled = state.isCountDownEnabled,
            )
        }
    }
}