package com.example.chatter.ui.animation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun Modifier.scaleInEffect(
    visible: Boolean,
    delay: Int = 0
): Modifier {
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 300,
            delayMillis = delay,
            easing = FastOutSlowInEasing
        )
    )
    return this.graphicsLayer(
        scaleX = scale,
        scaleY = scale,
        transformOrigin = TransformOrigin(0.5f, 0.5f)
    )
}

@Composable
fun Modifier.fadeInEffect(
    visible: Boolean,
    delay: Int = 0
): Modifier {
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 500,
            delayMillis = delay,
            easing = FastOutSlowInEasing
        )
    )
    return this.graphicsLayer(alpha = alpha)
}

@Composable
fun Modifier.slideInFromLeftEffect(
    visible: Boolean,
    delay: Int = 0
): Modifier {
    val offsetX by animateFloatAsState(
        targetValue = if (visible) 0f else -100f,
        animationSpec = tween(
            durationMillis = 500,
            delayMillis = delay,
            easing = FastOutSlowInEasing
        )
    )
    return this.graphicsLayer(translationX = offsetX)
}

@Composable
fun Modifier.pulseEffect(
    enabled: Boolean = true,
    initialScale: Float = 1f,
    pulseScale: Float = 1.05f,
    durationMillis: Int = 1000
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = initialScale,
        targetValue = pulseScale,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    if (enabled) {
        this.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    } else {
        this
    }
}
