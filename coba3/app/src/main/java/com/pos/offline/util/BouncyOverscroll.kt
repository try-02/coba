package com.pos.offline.util

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScrollModifierNode
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidatePlacement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Velocity
import io.iamjosephmj.flinger.configs.FlingConfiguration
import io.iamjosephmj.flinger.flings.flingBehavior
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sign

fun Modifier.bouncyOverscroll(orientation: Orientation = Orientation.Vertical): Modifier =
    this then BouncyOverscrollElement(orientation)

private data class BouncyOverscrollElement(
    val orientation: Orientation,
) : ModifierNodeElement<BouncyOverscrollNode>() {
    override fun create(): BouncyOverscrollNode = BouncyOverscrollNode(orientation)

    override fun update(node: BouncyOverscrollNode) {
        node.orientation = orientation
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "bouncyOverscroll"
        properties["orientation"] = orientation
    }
}

private class BouncyOverscrollNode(
    var orientation: Orientation,
) : DelegatingNode(), LayoutModifierNode {

    val translation = Animatable(0f)
    private var animJob: Job? = null

    private val connection = object : NestedScrollConnection {
        override fun onPreScroll(
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            val current = translation.value
            val availableDelta = if (orientation == Orientation.Vertical) available.y else available.x

            if (abs(current) > 0.5f && sign(availableDelta) != sign(current)) {
                animJob?.cancel()

                val maxConsumed =
                    if (current > 0) {
                        availableDelta.coerceAtLeast(-current)
                    } else {
                        availableDelta.coerceAtMost(-current)
                    }

                coroutineScope.launch {
                    translation.snapTo(translation.value + maxConsumed)
                    invalidatePlacement()
                }

                return if (orientation == Orientation.Vertical) {
                    Offset(0f, maxConsumed)
                } else {
                    Offset(maxConsumed, 0f)
                }
            }
            return Offset.Zero
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            val availableDelta = if (orientation == Orientation.Vertical) available.y else available.x

            if (availableDelta != 0f && source == NestedScrollSource.UserInput) {
                animJob?.cancel()

                val currentAbs = abs(translation.value)
                val elasticity = 0.45f * (1f - (currentAbs / 350f).coerceIn(0f, 0.85f))
                val resistance = availableDelta * elasticity

                coroutineScope.launch {
                    val target = (translation.value + resistance).coerceIn(-350f, 350f)
                    translation.snapTo(target)
                    invalidatePlacement()
                }
                return available
            }
            return Offset.Zero
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            val current = translation.value
            if (abs(current) > 0.5f) {
                val availableVelocity = if (orientation == Orientation.Vertical) available.y else available.x
                springBackToZero(initialVelocity = availableVelocity * 0.2f)
                return available
            }
            return Velocity.Zero
        }

        override suspend fun onPostFling(
            consumed: Velocity,
            available: Velocity,
        ): Velocity {
            val availableVelocity = if (orientation == Orientation.Vertical) available.y else available.x
            if (availableVelocity != 0f) {
                val initialVel = (availableVelocity * 0.4f).coerceIn(-3500f, 3500f)
                springBackToZero(initialVelocity = initialVel)
                return available
            }
            return Velocity.Zero
        }
    }

    init {
        delegate(nestedScrollModifierNode(connection, null))
    }

    fun springBackToZero(initialVelocity: Float = 0f) {
        animJob?.cancel()
        animJob =
            coroutineScope.launch {
                translation.animateTo(
                    targetValue = 0f,
                    initialVelocity = initialVelocity,
                    animationSpec =
                        spring(
                            dampingRatio = 0.5f,
                            stiffness = Spring.StiffnessLow,
                        ),
                ) {
                    invalidatePlacement()
                }
            }
    }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            placeable.placeWithLayer(0, 0) {
                val current = translation.value
                val absCurrent = abs(current)

                val scaleFactor = 1f + (absCurrent * 0.00012f).coerceAtMost(0.035f)
                scaleX = scaleFactor
                scaleY = scaleFactor

                if (orientation == Orientation.Vertical) {
                    translationY = current
                } else {
                    translationX = current
                }
            }
        }
    }

    override fun onReset() {
        super.onReset()
        animJob?.cancel()
        coroutineScope.launch {
            translation.snapTo(0f)
        }
    }

    override fun onDetach() {
        super.onDetach()
        animJob?.cancel()
    }
}

@Composable
fun iosGlideFlingBehavior(): FlingBehavior =
    flingBehavior(
        scrollConfiguration =
            FlingConfiguration
                .Builder()
                .scrollViewFriction(0.012f)
                .decelerationFriction(0.025f)
                .splineInflection(0.15f)
                .numberOfSplinePoints(150)
                .build(),
    )