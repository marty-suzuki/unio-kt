package com.github.marty_suzuki.unio

import kotlinx.coroutines.CoroutineScope

interface OutputFactory<
        Input : Unio.Input,
        Output : Unio.Output,
        Extra : Unio.Extra,
        State : Unio.State
        > {
    fun create(dependency: Dependency<Input, Extra, State>, viewModelScope: CoroutineScope): Output
}