package com.github.marty_suzuki.unio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.lang.Exception

open class UnioViewModel<Input : Unio.Input, Output : Unio.Output>(
    unioFactory: UnioFactory<Input, Output>
) : ViewModel(), UnidirectionalIO<Input, Output> {

    override val input: InputProxy<Input>
        get() = unio.input

    override val output: OutputProxy<Output>
        get() = unio.output

    private val _onCleared = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val unio = unioFactory.create(
        viewModelScope = viewModelScope,
        onCleared = _onCleared
    )

    override fun onCleared() {
        super.onCleared()
        try {
            _onCleared.tryEmit(Unit)
        } catch (e: Exception) {
            // Do nothing
        }
    }
}