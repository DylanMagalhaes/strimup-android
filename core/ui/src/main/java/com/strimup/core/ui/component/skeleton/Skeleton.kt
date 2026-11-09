package com.strimup.core.ui.component.skeleton

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

private const val SHIMMER_DURATION_MS = 1200
private const val SHIMMER_HIGHLIGHT_ALPHA = 0.08f
private const val SKELETON_BASE_ALPHA = 0.08f
private const val SHIMMER_WIDTH_RATIO = 0.6f

@Composable
fun Modifier.shimmerEffect(): Modifier {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val progress = transition.animateFloat(
        initialValue = -SHIMMER_WIDTH_RATIO,
        targetValue = 1f + SHIMMER_WIDTH_RATIO,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = SHIMMER_DURATION_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmer_progress",
    )
    val highlight = Color.White.copy(alpha = SHIMMER_HIGHLIGHT_ALPHA)

    return drawWithContent {
        drawContent()
        val bandWidth = size.width * SHIMMER_WIDTH_RATIO
        val start = size.width * progress.value
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(Color.Transparent, highlight, Color.Transparent),
                start = Offset(start - bandWidth, 0f),
                end = Offset(start, size.height),
            ),
        )
    }
}

@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
    color: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = SKELETON_BASE_ALPHA),
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(color)
            .shimmerEffect(),
    )
}
