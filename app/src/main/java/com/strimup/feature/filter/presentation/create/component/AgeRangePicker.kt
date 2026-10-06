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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.strimup.R
import com.strimup.core.ui.theme.StrimupTheme
import com.strimup.core.ui.theme.zalandoFontFamily
import kotlin.math.roundToInt

@Composable
fun AgeRangePicker(
    range: IntRange,
    modifier: Modifier = Modifier,
    minAge: Float = 18f,
    maxAge: Float = 80f,
    onRangeSelected: (IntRange) -> Unit = {},
) {
    val selectedMin = range.first
    val selectedMax = range.last

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        // Aligné sur le layout de ProfileEditRow
        Text(
            text = stringResource(R.string.filter_age_range),
            fontFamily = zalandoFontFamily,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = pluralStringResource(R.plurals.filter_age_range_value, selectedMax, selectedMin, selectedMax),
            fontFamily = zalandoFontFamily,
            style = MaterialTheme.typography.bodyLarge
        )

        RangeSlider(
            value = selectedMin.toFloat()..selectedMax.toFloat(),
            onValueChange = { sliderRange ->
                onRangeSelected(sliderRange.start.roundToInt()..sliderRange.endInclusive.roundToInt())
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
            range = 18..80,
            onRangeSelected = {}
        )
    }
}