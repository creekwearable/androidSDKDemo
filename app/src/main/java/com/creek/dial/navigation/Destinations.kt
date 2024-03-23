package com.creek.dial.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.creek.dial.sdkFunction.SdkFunction

interface Destinations {
    val icon: ImageVector? get() = null
    val route: String
}

object SdkFunction : Destinations {

    override val icon = Icons.Filled.Home

    override val route = "sdkFunction"
}


object Dial : Destinations {

    override val icon = Icons.Filled.ShoppingCart

    override val route = "dial"
}

val tabRowScreens = listOf(SdkFunction, Dial)