package com.popivyurii.photosharing.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Created by Yurii Popiv on 24.03.2022.
 */

private val gallery_red = Color(0xFFE30425)
private val gallery_white = Color.White
private val gallery_purple_700 = Color(0xFF720D5D)
private val gallery_purple_800 = Color(0xFF5D1049)
private val gallery_purple_900 = Color(0xFF4E0D3A)

val galleryColors = lightColors(
    primary = gallery_purple_800,
    secondary = gallery_red,
    surface = gallery_purple_900,
    onSurface = gallery_white,
    primaryVariant = gallery_purple_700
)

val LightThemeColors = lightColors(
    primary = Purple700,
    primaryVariant = Purple800,
    onPrimary = Color.White,
    secondary = Color.White,
    onSecondary = Color.Black,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    error = Red800,
    onError = Color.White
)

val BottomSheetShape = RoundedCornerShape(
    topStart = 20.dp,
    topEnd = 20.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

@Composable
fun PhotoSharingTheme(content: @Composable() () -> Unit) {
    MaterialTheme(
        colors = galleryColors,
        typography = Typography,
        shapes = Shapes(small = RoundedCornerShape(12.dp)),
        content = content
    )
}

@Composable
fun SignInTheme(content: @Composable() () -> Unit) {
    MaterialTheme(
        colors = LightThemeColors,
        typography = Typography,
        shapes = Shapes(small = RoundedCornerShape(12.dp)),
        content = content
    )
}