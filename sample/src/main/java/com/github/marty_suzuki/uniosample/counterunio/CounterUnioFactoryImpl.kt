package com.github.marty_suzuki.uniosample.counterunio

import com.github.marty_suzuki.unio.UnioFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
class CounterUnioFactoryImpl @Inject constructor() : UnioFactory<CounterUnioInput, CounterUnioOutput> {
    override fun create(
        viewModelScope: CoroutineScope,
        onCleared: Flow<Unit>,
    ) = CounterUnio(
        input = CounterUnioInput(),
        state = CounterUnio.State(),
        extra = CounterUnio.Extra(5),
        viewModelScope = viewModelScope
    )
}