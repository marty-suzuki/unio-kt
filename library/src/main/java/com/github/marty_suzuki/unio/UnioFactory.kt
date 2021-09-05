package com.github.marty_suzuki.unio

import kotlinx.coroutines.CoroutineScope

interface UnioFactory<Input : Unio.Input, Output : Unio.Output> {
    fun create(viewModelScope: CoroutineScope): UnioProperties<Input, Output>
}