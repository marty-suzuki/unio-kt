package com.github.marty_suzuki.unio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface UnioFactory<Input : Unio.Input, Output : Unio.Output> {
    fun create(
        viewModelScope: CoroutineScope,
        onCleared: Flow<Unit>,
    ): UnidirectionalIO<Input, Output>
}