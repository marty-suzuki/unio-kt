package com.github.marty_suzuki.unio

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlin.reflect.KProperty1

class Dependency<
        Input : Unio.Input,
        Extra : Unio.Extra,
        State : Unio.State
        > internal constructor(
    private val input: Input,
    val extra: Extra,
    val state: State
) {
    fun <T> getFlow(property: KProperty1<Input, SharedFlow<T>>): Flow<T> = property.get(input)
}
