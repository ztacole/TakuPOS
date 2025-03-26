package com.takumi.takupos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.takumi.takupos.core.shimmer

@Composable
fun CategoryCard(
    modifier: Modifier = Modifier,
    category: String,
    isSelected: Boolean,
    isLoading: Boolean = false
) {
    if (isLoading) {
        Box(
            modifier = modifier
                .height(40.dp)
                .width(72.dp)
                .clip(MaterialTheme.shapes.small)
                .shimmer()
        )
    }
    else {
        Box(
            modifier = modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.small
                )
                .background(
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.background,
                    shape = MaterialTheme.shapes.small
                )
                .shimmer(isLoading),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = category,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}