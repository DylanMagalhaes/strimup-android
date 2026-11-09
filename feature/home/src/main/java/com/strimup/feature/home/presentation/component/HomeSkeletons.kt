package com.strimup.feature.home.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.strimup.core.ui.component.skeleton.SkeletonBox
import com.strimup.core.ui.component.skeleton.shimmerEffect

const val CUT_CARD_ASPECT_RATIO = 9f / 16f
val CutCardWidth = 116.dp

private const val SKELETON_CUT_COUNT = 4
private const val SKELETON_TAG_COUNT = 3
private const val SKELETON_SOCIAL_COUNT = 3
private const val ON_DARK_ALPHA = 0.14f
private const val AVATAR_WIDTH_RATIO = 0.4f
private val PillShape = RoundedCornerShape(percent = 50)

@Composable
fun HomeHeroSkeleton(modifier: Modifier = Modifier) {
    val onDark = Color.White.copy(alpha = ON_DARK_ALPHA)
    Box(
        modifier = modifier
            .fillMaxSize()
            .shimmerEffect(),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SkeletonBox(modifier = Modifier.size(width = 52.dp, height = 20.dp), color = onDark)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SkeletonBox(modifier = Modifier.size(40.dp), shape = CircleShape, color = onDark)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SkeletonBox(modifier = Modifier.size(width = 140.dp, height = 16.dp), color = onDark)
                    SkeletonBox(modifier = Modifier.size(width = 220.dp, height = 12.dp), color = onDark)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SkeletonBox(modifier = Modifier.size(width = 120.dp, height = 38.dp), shape = PillShape, color = onDark)
                SkeletonBox(modifier = Modifier.size(width = 170.dp, height = 38.dp), shape = PillShape, color = onDark)
            }
        }
    }
}

@Composable
fun HomeCutsRowSkeleton(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SkeletonBox(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .size(width = 72.dp, height = 28.dp),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            userScrollEnabled = false,
        ) {
            items(SKELETON_CUT_COUNT) {
                SkeletonBox(
                    modifier = Modifier
                        .width(CutCardWidth)
                        .aspectRatio(CUT_CARD_ASPECT_RATIO),
                    shape = RoundedCornerShape(16.dp),
                )
            }
        }
    }
}

@Composable
fun HomeBannerSkeleton(modifier: Modifier = Modifier) {
    SkeletonBox(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(BannerHeight),
        shape = RoundedCornerShape(16.dp),
    )
}

@Composable
fun StreamerCardSkeleton(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                SkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth(AVATAR_WIDTH_RATIO)
                        .aspectRatio(1f),
                    shape = RoundedCornerShape(16.dp),
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(SKELETON_TAG_COUNT) {
                        SkeletonBox(modifier = Modifier.size(width = 84.dp, height = 24.dp), shape = PillShape)
                    }
                }
            }
            SkeletonBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
            )
            SkeletonBox(modifier = Modifier.size(width = 160.dp, height = 28.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(SKELETON_SOCIAL_COUNT) {
                    SkeletonBox(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(12.dp))
                }
            }
        }
    }
}
