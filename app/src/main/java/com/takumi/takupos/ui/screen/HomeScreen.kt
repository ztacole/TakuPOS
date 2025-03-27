package com.takumi.takupos.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.takumi.takupos.ui.components.CategoryCard
import com.takumi.takupos.ui.components.ProductCard
import com.takumi.takupos.ui.theme.TakuPOSTheme
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = true) {
        delay(3000)
        isLoading = false
    }

    val listState = rememberLazyGridState()
    var isHeaderVisible by remember { mutableStateOf(true) }
    var lastScrollOffset by remember { mutableIntStateOf(0) }

    LaunchedEffect(key1 = listState) {
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                val threshold = 50
                isHeaderVisible = index == 0 || offset < lastScrollOffset
                lastScrollOffset = offset
            }
    }

    val categories = listOf("Pouch", "Dress", "Shoes", "Shirt")
    val products = List(12) { "Baju Keren" }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        AnimatedVisibility(visible = isHeaderVisible) {
            CategorySection(
                categories = categories,
                isLoading = isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
        HorizontalDivider(
            thickness = 1.dp,
            modifier = Modifier.shadow(2.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = listState
        ) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    isLoading = isLoading
                )
            }
        }
    }
}

@Composable
fun CategorySection(
    modifier: Modifier = Modifier,
    categories: List<String>,
    isLoading: Boolean
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(categories) { index, category ->
            CategoryCard(
                category = category,
                isSelected = index == 0,
                isLoading = isLoading
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    TakuPOSTheme {
        HomeScreen()
    }
}