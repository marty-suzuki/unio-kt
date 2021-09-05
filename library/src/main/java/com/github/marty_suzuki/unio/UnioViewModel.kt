package com.github.marty_suzuki.unio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

open class UnioViewModel<Input : Unio.Input, Output : Unio.Output>(
    unioFactory: UnioFactory<Input, Output>
) : ViewModel(), UnioProperties<Input, Output> {

    override val input: InputProxy<Input>
        get() = unio.input

    override val output: OutputProxy<Output>
        get() = unio.output

    private val unio = unioFactory.create(viewModelScope)
}