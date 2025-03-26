package com.takumi.takupos.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.takumi.takupos.ui.components.CategoryCard
import com.takumi.takupos.ui.components.ProductCard
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    var isLoading by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(key1 = true) {
        delay(5000)
        isLoading = false
    }

        val categories = listOf(
            "Pouch",
            "Dress",
            "Shoes",
            "Shirt"
        )
        val products = listOf(
            "Baju Keren",
            "Baju Keren",
            "Baju Keren",
            "Baju Keren",
            "Baju Keren",
            "Baju Keren",
            "Baju Keren",
            "Baju Keren",
            "Baju Keren",
            "Baju Keren",
            "Baju Keren",
            "Baju Keren",
        )
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        CategorySection(
            categories = categories,
            isLoading = isLoading,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        HorizontalDivider()
        ProductSection(
            products = products,
            isLoading = isLoading
        )
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

@Composable
fun ProductSection(
    modifier: Modifier = Modifier,
    products: List<String>,
    isLoading: Boolean
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        userScrollEnabled = true
    ) {
        items(products) { product ->
            ProductCard(
                product = product,
                isLoading = isLoading
            )
        }
    }
}