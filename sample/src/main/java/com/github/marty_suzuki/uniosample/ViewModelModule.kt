package com.github.marty_suzuki.uniosample

import com.github.marty_suzuki.unio.UnioFactory
import com.github.marty_suzuki.uniosample.counterunio.CounterUnio
import com.github.marty_suzuki.uniosample.counterunio.CounterUnioInput
import com.github.marty_suzuki.uniosample.counterunio.CounterUnioOutput
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface ViewModelBindModule {
    @Binds
    fun bindCounterUnioFactory(
        unioFactory: CounterUnio.Factory
    ): UnioFactory<CounterUnioInput, CounterUnioOutput>
}

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelProvideModule {
    @Provides
    fun provideCounterUnioInput() = CounterUnioInput()

    @Provides
    fun provideCounterUnioState() = CounterUnio.State()
}