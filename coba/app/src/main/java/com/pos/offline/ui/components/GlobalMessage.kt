package com.pos.offline.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GlobalMessageController(
    private val scope: CoroutineScope,
) {
    var currentMessage by mutableStateOf<String?>(null)
        private set

    private var messageJob: Job? = null
    private var messageId = 0L

    fun showMessage(
        message: String,
        durationMillis: Long = 3000L,
    ) {
        messageJob?.cancel()

        val id = ++messageId
        currentMessage = message

        messageJob = scope.launch {
            delay(durationMillis.coerceAtLeast(0L))

            if (messageId == id) {
                currentMessage = null
                messageJob = null
            }
        }
    }

    fun dismiss() {
        messageJob?.cancel()
        messageJob = null
        messageId++
        currentMessage = null
    }
}

val LocalGlobalMessage = staticCompositionLocalOf<GlobalMessageController> {
    error("GlobalMessageController belum disediakan")
}

@Composable
fun TopAlignedMessagePill(
    message: String?,
    onDismiss: () -> Unit,
) {
    AnimatedVisibility(
        visible = message != null,
        enter =
            fadeIn(tween(300)) +
                slideInVertically(
                    animationSpec = tween(300),
                    initialOffsetY = { -it },
                ),
        exit =
            fadeOut(tween(300)) +
                slideOutVertically(
                    animationSpec = tween(300),
                    targetOffsetY = { -it },
                ),
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .statusBarsPadding(),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Surface(
                color = MaterialTheme.colorScheme.inverseSurface,
                contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                shape = RoundedCornerShape(50),
                shadowElevation = 8.dp,
                onClick = onDismiss,
            ) {
                Row(
                    modifier =
                        Modifier.padding(
                            horizontal = 20.dp,
                            vertical = 12.dp,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = message.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}