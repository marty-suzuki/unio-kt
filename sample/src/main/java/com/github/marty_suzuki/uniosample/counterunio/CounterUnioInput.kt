package com.github.marty_suzuki.uniosample.counterunio

import com.github.marty_suzuki.unio.Unio
import kotlinx.coroutines.flow.MutableSharedFlow

class CounterUnioInput : Unio.Input {
    val countUp = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val countDown = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
}