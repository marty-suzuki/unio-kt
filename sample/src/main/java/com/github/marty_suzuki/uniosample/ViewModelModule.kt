package com.github.marty_suzuki.uniosample

import com.github.marty_suzuki.unio.UnioFactory
import com.github.marty_suzuki.uniosample.counterunio.CounterUnio
import com.github.marty_suzuki.uniosample.counterunio.CounterUnioFactory
import com.github.marty_suzuki.uniosample.counterunio.CounterUnioFactoryImpl
import com.github.marty_suzuki.uniosample.counterunio.CounterUnioInput
import com.github.marty_suzuki.uniosample.counterunio.CounterUnioOutput
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface ViewModelModule {
    @Binds
    @CounterUnioFactory
    fun bindUnioFactory(
        unioFactory: CounterUnioFactoryImpl
    ): UnioFactory<CounterUnioInput, CounterUnioOutput>
}