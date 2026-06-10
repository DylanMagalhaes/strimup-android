package com.strimup.feature.home.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.common.ui.theme.zalandoFontFamily
import com.strimup.feature.home.presentation.HomeTab

@Composable
fun ContentSegmentedControl(
    modifier: Modifier = Modifier,
    isDiscoverySelected: Boolean,
    isInLiveSelected: Boolean,
    onButtonClick: (HomeTab) -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
        ) {
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDiscoverySelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            ),
            shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp, topEnd = 0.dp, bottomEnd = 0.dp),
            onClick = { onButtonClick(HomeTab.DISCOVERY) }
        ) {
            Text(
                text = "Découvrir",
                fontFamily = zalandoFontFamily,
                fontWeight = FontWeight.Bold,
            )
        }

        Button(

            colors = ButtonDefaults.buttonColors(
                containerColor = if (isInLiveSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            ),
            shape = RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 8.dp, bottomEnd = 8.dp),
            onClick = { onButtonClick(HomeTab.IN_LIVE) }

        ) {
            Text(
                text = "En live",
                fontFamily = zalandoFontFamily,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
@Preview
fun ContentSegmentedControlPreview() {
    StrimupTheme {
        ContentSegmentedControl(
            isDiscoverySelected = true,
            isInLiveSelected = false,
            onButtonClick = {}
        )
    }
}