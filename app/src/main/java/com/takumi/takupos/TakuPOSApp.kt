package com.takumi.takupos

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.takumi.takupos.core.Constants.SEARCH_HISTORY_KEY
import com.takumi.takupos.ui.components.TakuPOSBottomBar
import com.takumi.takupos.ui.navigation.Screen
import com.takumi.takupos.ui.screen.HistoryScreen
import com.takumi.takupos.ui.screen.HomeScreen
import com.takumi.takupos.ui.screen.ScanScreen
import com.takumi.takupos.ui.theme.TakuPOSTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun TakuPOSApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackstack by navController.currentBackStackEntryAsState()
    val currentRoute = navBackstack?.destination?.route

    var isSearchActive by remember {
        mutableStateOf(false)
    }

    Scaffold(
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            if (currentRoute == Screen.Home.route) HomeTopBar(onSearchStateChange = {
                isSearchActive = it
            })
        },
        bottomBar = {
            if (!isSearchActive && (currentRoute == Screen.Home.route || currentRoute == Screen.History.route)) {
                TakuPOSBottomBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.History.route) {
                HistoryScreen()
            }
            composable(Screen.Scan.route) {
                ScanScreen()
            }
        }
    }
}

@Composable
fun HomeTopBar(
    modifier: Modifier = Modifier,
    onSearchStateChange: (Boolean)-> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(
                72.dp + WindowInsets.statusBars
                    .asPaddingValues()
                    .calculateTopPadding()
            )
            .background(color = MaterialTheme.colorScheme.primary)
            .padding(8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Image(
            painter = painterResource(id = R.drawable.motif),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TakuSearchBar(
                text = "", onTextChange = {}, hint = "Cari produk..", modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .size(32.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.cart_outlined),
                    contentDescription = "Keranjang",
                    modifier = Modifier,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(
    onSearchStateChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var query by remember {
        mutableStateOf("")
    }
    var isActive by remember {
        mutableStateOf(false)
    }

    SearchBar(
        query = query,
        onQueryChange = { query = it },
        onSearch = {
            scope.launch {
                saveSearchQuery(context, query)
            }
        },
        active = isActive,
        onActiveChange = {
            isActive = it
            onSearchStateChange(it)
        },
        leadingIcon = {
            if (isActive) IconButton(onClick = {
                isActive = false
                onSearchStateChange(false)
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Kembali",
                    tint = Color.Gray
                )
            }
            else Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Cari",
                tint = Color.Gray
            )
        },
        trailingIcon = {
            if (isActive) IconButton(onClick = {
                if (query.isEmpty()) {
                    isActive = false
                    onSearchStateChange(false)
                }
                else query = ""
            }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Hapus",
                    tint = Color.Gray
                )
            }
        },
        placeholder = {
            Text(
                text = "Cari produk disini!",
                color = MaterialTheme.colorScheme.secondary
            )
        },
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background,
            inputFieldColors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.background
            )
        )
    ) {
        val searchHistory by getSearchHistory(context).collectAsState(initial = emptyList())

        searchHistory.takeLast(4).reversed().forEach {
            ListItem(
                headlineContent = { 
                    Text(text = it)
                },
                leadingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.history),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                },
                modifier = Modifier.clickable {
                    query = it
                }
            )
        }
    }
}

@Composable
fun TakuSearchBar(
    modifier: Modifier = Modifier,
    text: String,
    onTextChange: (String)-> Unit,
    hint: String,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier,
        enabled = enabled,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            focusedContainerColor = MaterialTheme.colorScheme.background,
            unfocusedBorderColor = Color.LightGray,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.secondary,
            focusedPlaceholderColor = MaterialTheme.colorScheme.secondary,
            disabledPlaceholderColor = MaterialTheme.colorScheme.secondary
        ),
        textStyle = TextStyle(fontSize = 12.sp),
        shape = MaterialTheme.shapes.medium,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary
            )
        },
        placeholder = { Text(text = hint) }
    )
}

@Preview(showBackground = true)
@Composable
private fun PrevSearchBar() {
    TakuPOSTheme {
        TakuSearchBar(text = "Test", onTextChange = {}, hint = "Tes")
    }
}

val Context.searchHistory: DataStore<Preferences> by preferencesDataStore(name = SEARCH_HISTORY_KEY)

suspend fun saveSearchQuery(context: Context, query: String) {
    context.searchHistory.edit { preferences ->
        val history = preferences[stringSetPreferencesKey(SEARCH_HISTORY_KEY)]?.toList() ?: emptyList()
        val newHistory = (history + query).distinct().takeLast(10)
        preferences[stringSetPreferencesKey(SEARCH_HISTORY_KEY)] = newHistory.toSet()
    }
}

fun getSearchHistory(context: Context): Flow<List<String>> {
    return context.searchHistory.data.map { preferences ->
        preferences[stringSetPreferencesKey(SEARCH_HISTORY_KEY)]?.toList()?.takeLast(10) ?: emptyList()
    }
}