package com.popivyurii.photosharing.gallery

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch

/**
 * Created by Yurii Popiv on 28.03.2022.
 */

typealias OnGalleryItemClicked = (PhotoModel) -> Unit

enum class GalleryScreen {
    All, Shared
}

@Composable
fun GalleryHome(
    onGalleryItemClicked: OnGalleryItemClicked,
    onDateSelectionClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.statusBarsPadding()
    ) {
        val scope = rememberCoroutineScope()
        GalleryHomeContent(
            modifier = modifier,
            onGalleryItemClicked = onGalleryItemClicked,
            openDrawer = {
                scope.launch {
                    scaffoldState.drawerState.open()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GalleryHomeContent(
    onGalleryItemClicked: OnGalleryItemClicked,
    openDrawer: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GalleryViewModel = viewModel(),
) {
    var tabSelected by remember { mutableStateOf(GalleryScreen.All) }

    val localPhotos by viewModel.localPhotos.observeAsState()

    BackdropScaffold(
        modifier = modifier,
        scaffoldState = rememberBackdropScaffoldState(BackdropValue.Revealed),
        frontLayerScrimColor = Color.Unspecified,
        appBar = {
            HomeTabBar(openDrawer, tabSelected, onTabSelected = { tabSelected = it })
        },
        backLayerContent = {

        },
        frontLayerContent = {

            when (tabSelected) {
                GalleryScreen.All -> {
                    localPhotos?.let { localPhotos ->
                        ExploreSection(
                            title = "Local Photos",
                            photoList = localPhotos,
                            onGalleryItemClicked = onGalleryItemClicked
                        )
                    }
                }
                GalleryScreen.Shared -> {
//                    ExploreSection(
//                        title = "Storage Photos",
//                        photoList = localPhotos,
//                        onGalleryItemClicked = onGalleryItemClicked
//                    )
                }
            }
        }
    )
}
@Composable
private fun HomeTabBar(
    openDrawer: () -> Unit,
    tabSelected: GalleryScreen,
    onTabSelected: (GalleryScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    GalleryTabBar(
        modifier = modifier,
        onMenuClicked = openDrawer
    ) { tabBarModifier ->
        GalleryTabs(
            modifier = tabBarModifier,
            titles = GalleryScreen.values().map { it.name },
            tabSelected = tabSelected,
            onTabSelected = { newTab -> onTabSelected(GalleryScreen.values()[newTab.ordinal]) }
        )
    }
}