package fr.delcey.logino.data.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class TestCoroutineRule : TestRule {

    private val testCoroutineScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testCoroutineScheduler)
    private val testCoroutineScope = TestScope(testDispatcher)

    override fun apply(base: Statement, description: Description): Statement = object : Statement() {
        @Throws(Throwable::class)
        override fun evaluate() {
            Dispatchers.setMain(testDispatcher)

            base.evaluate()

            Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        }
    }

    fun runTest(block: suspend TestScope.() -> Unit) = testCoroutineScope.runTest {
        block()
    }
}