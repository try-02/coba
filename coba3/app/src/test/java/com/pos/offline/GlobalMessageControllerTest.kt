package com.pos.offline

import com.pos.offline.ui.components.GlobalMessageController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
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

        controller.showMessage(
            message = "Pesan",
            durationMillis = 3_000L,
        )

        // Tepat sebelum 3 detik, pesan masih harus terlihat.
        scope.advanceTimeBy(2_999L)

        assertEquals("Pesan", controller.currentMessage)

        // Mencapai tepat 3 detik.
        scope.advanceTimeBy(1L)

        // Jalankan coroutine yang dijadwalkan tepat pada t=3000.
        scope.runCurrent()

        assertNull(controller.currentMessage)
    }

    @Test
    fun negativeDuration_isSafeAndDismisses() {
        val (scope, controller) = controller()

        controller.showMessage(
            message = "Pesan",
            durationMillis = -1L,
        )

        scope.advanceUntilIdle()

        assertNull(controller.currentMessage)
    }

    @Test
    fun newerMessage_replacesOlderMessage() {
        val (scope, controller) = controller()

        // t=0
        controller.showMessage(
            message = "A",
            durationMillis = 3_000L,
        )

        // t=1000
        scope.advanceTimeBy(1_000L)

        // A dibatalkan dan B dimulai.
        controller.showMessage(
            message = "B",
            durationMillis = 3_000L,
        )

        assertEquals("B", controller.currentMessage)

        // t=3000
        // B baru berjalan 2 detik.
        scope.advanceTimeBy(2_000L)

        assertEquals("B", controller.currentMessage)

        // t=4000
        // Tepat 3 detik sejak B.
        scope.advanceTimeBy(1_000L)
        scope.runCurrent()

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

        // t=0
        controller.showMessage(
            message = "A",
            durationMillis = 3_000L,
        )

        // t=1000
        scope.advanceTimeBy(1_000L)

        // Batalkan A.
        controller.dismiss()

        // Tampilkan B.
        controller.showMessage(
            message = "B",
            durationMillis = 3_000L,
        )

        assertEquals("B", controller.currentMessage)

        // t=3000
        // B baru berjalan 2 detik.
        scope.advanceTimeBy(2_000L)

        assertEquals("B", controller.currentMessage)

        // t=4000
        // Tepat 3 detik sejak B.
        scope.advanceTimeBy(1_000L)
        scope.runCurrent()

        assertNull(controller.currentMessage)
    }
}