package com.github.marty_suzuki.uniosample.counterunio

import com.github.marty_suzuki.unio.Unio
import kotlinx.coroutines.flow.Flow

class CounterUnioOutput(
    val count: Flow<String>,
    val isCountDownEnabled: Flow<Boolean>,
) : Unio.Output