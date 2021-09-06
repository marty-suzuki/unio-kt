package com.github.marty_suzuki.unio

import kotlinx.coroutines.CoroutineScope

abstract class Unio<
        Input : Unio.Input,
        Output : Unio.Output,
        Extra : Unio.Extra,
        State : Unio.State
        >(
    input: Input,
    extra: Extra,
    state: State,
    viewModelScope: CoroutineScope,

) : UnioProperties<Input, Output>, UnioBinder<Input, Output, Extra, State> {
    interface Input
    interface Output
    interface Extra
    interface State

    object NoExtra : Extra
    object NoState : State

    final override val input = InputProxy(input)
    final override val output = Dependency(
        input = input,
        extra = extra,
        state = state,
    ).let { OutputProxy(bind(it, viewModelScope)) }
}

interface UnioProperties<Input : Unio.Input, Output : Unio.Output> {
    val input: InputProxy<Input>
    val output: OutputProxy<Output>
}

interface UnioBinder<
        Input : Unio.Input,
        Output : Unio.Output,
        Extra : Unio.Extra,
        State : Unio.State
        > {
    fun bind(dependency: Dependency<Input, Extra, State>, viewModelScope: CoroutineScope): Output
}