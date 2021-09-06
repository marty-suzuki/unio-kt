package com.github.marty_suzuki.uniosample.counterunio

import com.github.marty_suzuki.unio.UnioFactory
import com.github.marty_suzuki.unio.UnioViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Qualifier

@HiltViewModel
class CounterViewModel @Inject constructor(
    unioFactory: UnioFactory<CounterUnioInput, CounterUnioOutput>,
) : UnioViewModel<CounterUnioInput, CounterUnioOutput>(unioFactory)