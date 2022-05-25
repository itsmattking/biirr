package me.mking.biirr

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle

@ExperimentalCoroutinesApi
fun <T> TestScope.withFlow(flow: Flow<T>, testBody: (TestFlowScope<T>.() -> Unit)? = null) =
    TestFlowScope<T>(this).runTest(flow, testBody)

@ExperimentalCoroutinesApi
class TestFlowScope<T>(private val scope: TestScope) {
    private val _result: MutableList<T> = mutableListOf()
    private var job: Job? = null

    fun runTest(flow: Flow<T>, testBody: (TestFlowScope<T>.() -> Unit)? = null) {
        job = flow
            .onEach { _result.add(it) }
            .launchIn(scope + UnconfinedTestDispatcher(scope.testScheduler))

        testBody?.invoke(this@TestFlowScope) ?: advance()
        job?.cancel()
    }

    val result: List<T>
        get() {
            advance()
            return _result
        }

    fun advance() = scope.advanceUntilIdle()
}
