package com.github.marty_suzuki.unio

interface UnidirectionalIO<Input : Unio.Input, Output : Unio.Output> {
    val input: InputProxy<Input>
    val output: OutputProxy<Output>
}