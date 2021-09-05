package com.github.marty_suzuki.uniosample.counterunio

import com.github.marty_suzuki.unio.UnioFactory
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class CounterUnioFactoryImpl @Inject constructor() : UnioFactory<CounterUnioInput, CounterUnioOutput> {
    override fun create(viewModelScope: CoroutineScope) = CounterUnio(
        input = CounterUnioInput(),
        state = CounterUnio.State(),
        extra = CounterUnio.Extra(5),
        viewModelScope = viewModelScope
    )
}