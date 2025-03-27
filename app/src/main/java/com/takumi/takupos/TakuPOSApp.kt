package com.takumi.takupos

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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
import com.takumi.takupos.ui.screen.HomeScreen
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
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
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
        }
    }
}

@Composable
fun HomeTopBar(
    modifier: Modifier = Modifier,
    onSearchStateChange: (Boolean)-> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Search(onSearchStateChange)
        IconButton(
            onClick = { /*TODO*/ },
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.cart_outlined),
                contentDescription = "Keranjang",
                modifier = modifier.size(32.dp)
            )
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
        }
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