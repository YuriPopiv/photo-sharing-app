/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.popivyurii.photosharing.gallery

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter.State.Loading
import coil.compose.rememberImagePainter
import com.popivyurii.photosharing.R
import com.popivyurii.photosharing.theme.BottomSheetShape
import java.io.File

@Immutable
data class City(
    val name: String,
    val country: String,
    val latitude: String,
    val longitude: String
) {
    val nameToDisplay = "$name, $country"
}

@Immutable
data class PhotoModel(
    var name: String? = null,
    var path: String? = null,
    var folderName: String? = null,
    var size: String? = null,
    var uri: String? = null,
    var selected: Boolean = false
)

@Composable
fun ExploreSection(
    modifier: Modifier = Modifier,
    title: String,
    photoList: List<PhotoModel>,
    onGalleryItemClicked: OnGalleryItemClicked
) {
    Surface(modifier = modifier.fillMaxSize(), color = Color.White, shape = BottomSheetShape) {
        Column(modifier = Modifier.padding(start = 24.dp, top = 20.dp, end = 24.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.caption.copy(color = Color.DarkGray)
            )
            Spacer(Modifier.height(8.dp))
            LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 128.dp)){
                items(photoList){ exploreItem ->
                        ExploreItem(
                            modifier = Modifier.fillMaxWidth(),
                            item = exploreItem,
                            onItemClicked = onGalleryItemClicked
                        )
                        //Divider(color = Color.LightGray)
                }
                item {
                    Spacer(
                        modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars)
                    )
                }
            }
//            LazyColumn(
//                modifier = Modifier.weight(1f),
//            )
//            {
//                items(exploreList) { exploreItem ->
//                    Column(Modifier.fillParentMaxWidth()) {
//                        ExploreItem(
//                            modifier = Modifier.fillParentMaxWidth(),
//                            item = exploreItem,
//                            onItemClicked = onGalleryItemClicked
//                        )
//                        Divider(color = Color.LightGray)
//                    }
//                }
//                item {
//                    Spacer(
//                        modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars)
//                    )
//                }
//            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun ExploreItem(
    modifier: Modifier = Modifier,
    item: PhotoModel,
    onItemClicked: OnGalleryItemClicked
) {
    Row(
        modifier = modifier
            .clickable { onItemClicked(item) }
            .padding(top = 12.dp, bottom = 12.dp)
    ) {
        ExploreImageContainer {
            Box {
//                val painter = rememberImagePainter(
//                    data = item.path,
//                    builder = {
//                        crossfade(true)
//                    }
//                )
                Image(
                    painter = rememberImagePainter(data = File(item.path)),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )

//                if (painter.state is Loading) {
//                    Image(
//                        painter = painterResource(id = R.drawable.ic_logo),
//                        contentDescription = null,
//                        modifier = Modifier
//                            .size(36.dp)
//                            .align(Alignment.Center),
//                    )
//                }
            }
        }
//        Spacer(Modifier.width(24.dp))
//        Column {
//            Text(
//                text = item.city.country,
//                style = MaterialTheme.typography.h6
//            )
//            Spacer(Modifier.height(8.dp))
//            Text(
//                text = item.description,
//                style = MaterialTheme.typography.caption.copy(color = Color.DarkGray)
//            )
//        }
    }
}

@Composable
private fun ExploreImageContainer(content: @Composable () -> Unit) {
    Surface(Modifier.size(width = 160.dp, height = 160.dp), RoundedCornerShape(4.dp)) {
        content()
    }
}
