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
    outputFactory: OutputFactory<Input, Output, Extra, State>,
    viewModelScope: CoroutineScope,

) : UnidirectionalIO<Input, Output> {
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
    ).let { OutputProxy(outputFactory.create(it, viewModelScope)) }
}