package com.dicoding.mynavdrawer

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dicoding.mynavdrawer.ui.theme.MyNavDrawerTheme

data class MenuItem(val title: String, val icon: ImageVector)

@Composable
fun MyNavDrawerApp() {
    val items = listOf(
        MenuItem(title = stringResource(id = R.string.home), icon = Icons.Default.Home),
        MenuItem(title = stringResource(id = R.string.favourite), icon = Icons.Default.Favorite),
        MenuItem(title = stringResource(id = R.string.profile), icon = Icons.Default.AccountCircle)
    )
    val selectedItem = remember {
        mutableStateOf(items.first())
    }
    val appState = rememberMyNavDrawerState()
//    BackHandler(enabled = drawerState
    BackPressHandler(enabled = appState.drawerState.isOpen) {
        appState.onBackPress()
    }
    Scaffold(
        snackbarHost = { SnackbarHost(appState.snackbarHostState) },
        topBar = {
            MyTopBar { appState.onMenuClick() }
        }
    ) {
        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet {
                    Spacer(modifier = Modifier.height(12.dp))
                    items.forEach { item ->
                        NavigationDrawerItem(
                            label = { Text(text = item.title) },
                            selected = item == selectedItem.value,
                            onClick = {
                                appState.onItemClick(item)
                                selectedItem.value = item
                            },
                            modifier = Modifier.padding(12.dp),
                            icon = { Icon(imageVector = item.icon, contentDescription = null) }
                        )
                    }
                }
            },
            modifier = Modifier.padding(it),
            drawerState = appState.drawerState,
            gesturesEnabled = appState.drawerState.isOpen
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(id = if (appState.drawerState.isOpen) R.string.swipe_to_close else R.string.swipe_to_open))
            }
        }
    }
}

@Composable
fun BackPressHandler(enabled: Boolean = true, onBackPressed: () -> Unit) {
    val currentBackPressed by rememberUpdatedState(onBackPressed)
    val backCallback = remember {
        object: OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                currentBackPressed()
            }
        }
    }

    SideEffect {
        backCallback.isEnabled = enabled
    }
    val backDispatcher = checkNotNull(LocalOnBackPressedDispatcherOwner.current) {
        "No OnBackPressedDispatcherOwner was provided via LocalOnBackPressedDispatcherOwner"
    }.onBackPressedDispatcher
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, backDispatcher) {
        backDispatcher.addCallback(lifecycleOwner, backCallback)
        onDispose {
            backCallback.remove()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar(onMenuClick: () -> Unit) {
    TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) }, navigationIcon = {
        IconButton(onClick = { onMenuClick() }) {
            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
        }
    })
}

@Composable
@Preview(showBackground = true)
fun MyNavDrawerAppPreview() {
    MyNavDrawerTheme {
        MyNavDrawerApp()
    }
}