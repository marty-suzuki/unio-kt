package com.github.marty_suzuki.unio

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

@ExperimentalCoroutinesApi
class UnioTest {

    private class MockInput : Unio.Input {
        val intSharedFlow = MutableSharedFlow<Int>(extraBufferCapacity = 1)
        val stringSharedFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)
        val unitSharedFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    }

    @Test
    fun test_InputProxy() = runBlockingTest {
        val input = MockInput()
        val testTarget = InputProxy(input)

        val expectedInt = 1
        var actualInt: Int? = null
        val jobForIntTest = input.intSharedFlow
            .onEach { actualInt = it }
            .launchIn(this)
        testTarget.getLambda(MockInput::intSharedFlow).invoke(expectedInt)
        assertEquals(expectedInt, actualInt)
        jobForIntTest.cancel()

        val expectedString = "some string"
        var actualString: String? = null
        val jobFotStringTest = input.stringSharedFlow
            .onEach { actualString = it }
            .launchIn(this)
        testTarget.getLambda(MockInput::stringSharedFlow).invoke(expectedString)
        assertEquals(expectedString, actualString)
        jobFotStringTest.cancel()

        var actualUnit: Unit? = null
        val jobForUnitTest = input.unitSharedFlow
            .onEach { actualUnit = it }
            .launchIn(this)
        testTarget.getLambda(MockInput::unitSharedFlow).invoke()
        assertNotNull(actualUnit)
        jobForUnitTest.cancel()
    }

    private class MockOutput: Unio.Output {
        val sharedFlow = MutableSharedFlow<Int>(extraBufferCapacity = 1)
        val stateFlow = MutableStateFlow(0)
        val computedValue = Computed(0)
        val computedFun = Computed<(Int) -> Int> { { it + 1 } }
        val computedSuspendFun = Computed<suspend (Int) -> Int> { { it * it } }
    }

    @Test
    fun test_OutputProxy() = runBlockingTest {
        val output = MockOutput()
        val testTarget = OutputProxy(output)

        // Trying to access emit is compile error because those are not MutableSharedFlow type.
        // testTarget.getFlow(MockOutput::sharedFlow).emit(1)
        // testTarget.getSharedFlow(MockOutput::sharedFlow).emit(1)

        var actualSharedFlowValue: Int? = null
        val expectedSharedFlowValue = 1
        val jobForSharedFlow = testTarget.getSharedFlow(MockOutput::sharedFlow)
            .onEach { actualSharedFlowValue = it }
            .launchIn(this)
        output.sharedFlow.emit(expectedSharedFlowValue)
        assertEquals(expectedSharedFlowValue, actualSharedFlowValue)
        jobForSharedFlow.cancel()

        // Trying to access get value is compile error because that is not StateFlow type.
        // testTarget.getFlow(MockOutput::stateFlow).value

        // Trying to access set value is compile error because that is not MutableStateFlow type.
        // testTarget.getStateFlow(MockOutput::stateFlow).value = 0

        val expectedStateFlowValue = 1
        output.stateFlow.emit(expectedStateFlowValue)
        assertEquals(expectedSharedFlowValue, testTarget.getStateFlow(MockOutput::stateFlow).value)

        val computedValue = testTarget.getComputed(MockOutput::computedValue)
        assertEquals(0, computedValue)

        val computedFun = testTarget.getComputed(MockOutput::computedFun)
        assertEquals(2, computedFun.invoke(1))

        val computedSuspendFun = testTarget.getComputed(MockOutput::computedSuspendFun)
        assertEquals(4, computedSuspendFun.invoke(2))
    }
}