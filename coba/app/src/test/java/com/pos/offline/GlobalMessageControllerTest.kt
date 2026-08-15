package com.pos.offline

import com.pos.offline.ui.components.GlobalMessageController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GlobalMessageControllerTest {

    private fun controller(): Pair<TestScope, GlobalMessageController> {
        val dispatcher = StandardTestDispatcher()
        val scope = TestScope(dispatcher)
        return scope to GlobalMessageController(scope)
    }

    @Test
    fun initialMessage_isNull() {
        val (_, controller) = controller()
        assertNull(controller.currentMessage)
    }

    @Test
    fun showMessage_setsMessage() {
        val (_, controller) = controller()
        controller.showMessage("Pesan")

        assertEquals("Pesan", controller.currentMessage)
    }

    @Test
    fun showMessage_autoDismissesAfterDuration() {
        val (scope, controller) = controller()
        controller.showMessage("Pesan", durationMillis = 3_000)

        scope.advanceTimeBy(2_999)
        assertEquals("Pesan", controller.currentMessage)

        scope.advanceTimeBy(1)
        scope.advanceUntilIdle()
        assertNull(controller.currentMessage)
    }

    @Test
    fun negativeDuration_isSafeAndDismisses() {
        val (scope, controller) = controller()
        controller.showMessage("Pesan", durationMillis = -1)

        scope.advanceUntilIdle()
        assertNull(controller.currentMessage)
    }

    @Test
    fun newerMessage_replacesOlderMessage() {
        val (scope, controller) = controller()
        controller.showMessage("A", durationMillis = 3_000)

        scope.advanceTimeBy(1_000)
        controller.showMessage("B", durationMillis = 3_000)

        scope.advanceTimeBy(2_000)
        scope.advanceUntilIdle()

        assertEquals("B", controller.currentMessage)

        scope.advanceTimeBy(1_000)
        scope.advanceUntilIdle()
        assertNull(controller.currentMessage)
    }

    @Test
    fun dismiss_clearsMessageImmediately() {
        val (_, controller) = controller()
        controller.showMessage("Pesan")

        controller.dismiss()

        assertNull(controller.currentMessage)
    }

    @Test
    fun dismissedMessage_cannotClearLaterMessage() {
        val (scope, controller) = controller()
        controller.showMessage("A", durationMillis = 3_000)
        scope.advanceTimeBy(1_000)

        controller.dismiss()
        controller.showMessage("B", durationMillis = 3_000)

        scope.advanceTimeBy(2_000)
        scope.advanceUntilIdle()

        assertEquals("B", controller.currentMessage)
    }
}
