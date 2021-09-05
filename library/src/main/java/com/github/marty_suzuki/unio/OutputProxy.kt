package com.github.marty_suzuki.unio

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KProperty1

class OutputProxy<Output : Unio.Output>(
    private val output: Output
) {
    fun <T> getFlow(property: KProperty1<Output, Flow<T>>): Flow<T> = property(output)
    fun <T> getStateFlow(property: KProperty1<Output, StateFlow<T>>): StateFlow<T> = property(output)
    fun <T> getSharedFlow(property: KProperty1<Output, SharedFlow<T>>): SharedFlow<T> = property(output)
    fun <T> getValue(property: KProperty1<Output, StateFlow<T>>): T = property(output).value
    fun <T> getComputed(property: KProperty1<Output, Computed<T>>): T = property(output).value
}