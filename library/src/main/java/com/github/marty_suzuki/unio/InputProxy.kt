package com.github.marty_suzuki.unio

import kotlinx.coroutines.flow.MutableSharedFlow
import java.lang.Exception
import kotlin.reflect.KProperty1

class InputProxy<Input : Unio.Input>(
    private val input: Input
) {
    fun <T> getLambda(property: KProperty1<Input, MutableSharedFlow<T>>): (T) -> Unit {
        val sharedFlow = property(input)
        return {
            try {
                sharedFlow.tryEmit(it)
            } catch (e: Exception) {
                // Do nothing
            }
        }
    }

    fun getLambda(property: KProperty1<Input, MutableSharedFlow<Unit>>): () -> Unit {
        val sharedFlow = property(input)
        return {
            try {
                sharedFlow.tryEmit(Unit)
            } catch (e: Exception) {
                // Do nothing
            }
        }
    }
}