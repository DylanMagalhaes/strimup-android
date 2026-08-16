package com.strimup.feature.filter.presentation.create.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.strimup.common.ui.theme.StrimupTheme
import com.strimup.common.ui.theme.zalandoFontFamily
import kotlin.math.roundToInt

@Composable
fun AgeRangePicker(
    minAge: Float = 18f,
    maxAge: Float = 80f,
    modifier: Modifier = Modifier,
    onRangeSelected: (IntRange) -> Unit = {}
) {
    var sliderPosition by remember { mutableStateOf(18f..40f) }

    val selectedMin = sliderPosition.start.roundToInt()
    val selectedMax = sliderPosition.endInclusive.roundToInt()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        // Aligné sur le layout de ProfileEditRow
        Text(
            text = "Tranche d'âge",
            fontFamily = zalandoFontFamily,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "$selectedMin - $selectedMax ans",
            fontFamily = zalandoFontFamily,
            style = MaterialTheme.typography.bodyLarge
        )

        RangeSlider(
            value = sliderPosition,
            onValueChange = { range ->
                sliderPosition = range
                onRangeSelected(range.start.roundToInt()..range.endInclusive.roundToInt())
            },
            valueRange = minAge..maxAge,
            steps = (maxAge - minAge).toInt() - 1,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AgeRangePickerPreview() {
    StrimupTheme {
        AgeRangePicker(
            onRangeSelected = {}
        )
    }
}