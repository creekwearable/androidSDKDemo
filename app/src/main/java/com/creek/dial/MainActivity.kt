package com.creek.dial

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.LocalContentColor
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.creek.dial.customDial.CustomDialScreen
import com.creek.dial.dial.DialScreen
import com.creek.dial.navigation.Dial
import com.creek.dial.navigation.SdkFunction
import com.creek.dial.navigation.tabRowScreens
import com.creek.dial.scanDevice.ScanDeviceScreen
import com.creek.dial.sdkFunction.SdkFunction
import com.creek.dial.sendCommand.SendCommandScreen
import com.creek.dial.ui.theme.Creek_dial_androidTheme
import com.example.mylibrary.CreekManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import java.util.Locale

class MainActivity : ComponentActivity() {

    private val REQUEST_BLUETOOTH_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(

        )
        super.onCreate(savedInstanceState)
        CreekManager.sInstance.creekRegister(this)
        CreekManager.sInstance.initSDK()
        CreekManager.sInstance.listenDeviceState { status, deviceName ->
            Log.w("123456", "$status++++$deviceName")
        }
        CreekManager.sInstance.noticeUpdateListen {
            Log.w("123456", it.toString())
        }
        CreekManager.sInstance.exceptionListen {
            Log.w("123456", it)
        }
        CreekManager.sInstance.eventReportListen {

        }
        setContent {
            Creek_dial_androidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }



         fun requestBluetoothPermissions() {
            val permissions = arrayOf(
                // Manifest.permission.BLUETOOTH,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_MEDIA_LOCATION

            )

            ActivityCompat.requestPermissions(this, permissions, REQUEST_BLUETOOTH_PERMISSION)
        }

         fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

            if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 用户授予了蓝牙权限，可以执行相关操作

                } else {
                    // 用户拒绝了蓝牙权限，可以显示一条提示信息或执行其他操作
                    //  Toast.makeText(this, "拒绝了蓝牙权限所以不能扫描设备", Toast.LENGTH_SHORT).show()
                }
            }
        }

         fun hasBluetoothPermissions(): Boolean {
            //compileSdkVersion项目中编译SDK版本大于30申请以下权限可使用
            //Manifest.permission.BLUETOOTH_SCAN、Manifest.permission.BLUETOOTH_ADVERTISE、Manifest.permission.BLUETOOTH_CONNECT
            //若小于30可以直接使用权限对应的字符串
            if (Build.VERSION.SDK_INT > 30) {
                if ((ContextCompat.checkSelfPermission(
                        this,
                        "android.permission.BLUETOOTH_SCAN"
                    ) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                        this, "android.permission.BLUETOOTH_ADVERTISE"
                    )
                            != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                        this, "android.permission.ACCESS_COARSE_LOCATION"
                    )
                            != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                        this,
                        "android.permission.BLUETOOTH_CONNECT"
                    ) != PackageManager.PERMISSION_GRANTED)
                ) {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(
                            "android.permission.BLUETOOTH_SCAN",
                            "android.permission.BLUETOOTH_ADVERTISE",
                            "android.permission.BLUETOOTH_CONNECT"
                        ), REQUEST_BLUETOOTH_PERMISSION
                    )
                    return false
                }
            }
            return true
        }
    }
}





@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(bottomBar = { BottomBar(navController) }) { innerPadding ->
        NavHost(
            startDestination = "home",
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        ) {

            navigation(route = "home", startDestination = SdkFunction.route) {
                homeGraph(navController)
            }

            composable(
                "customDial/{titleName}/{width}/{height}/{cornerRadius}",
                arguments = listOf(
                    navArgument("titleName") { type = NavType.StringType },
                    navArgument("width") { type = NavType.IntType },
                    navArgument("height") { type = NavType.IntType },
                    navArgument("cornerRadius") { type = NavType.IntType }
                )
            ) {backStackEntry ->
                val titleName = backStackEntry.arguments?.getString("titleName") ?: ""
                val width = backStackEntry.arguments?.getInt("width") ?: 0
                val height = backStackEntry.arguments?.getInt("height") ?: 0
                val cornerRadius = backStackEntry.arguments?.getInt("cornerRadius") ?: 0
                CustomDialScreen(navController,titleName, width = width, height = height, cornerRadius = cornerRadius)
            }

            composable(
                "sendCommand/{functionStr}",
                arguments = listOf(navArgument("functionStr") { type = NavType.StringType })
            ) { backStackEntry ->
                val functionStr = backStackEntry.arguments?.getString("functionStr") ?: ""
                SendCommandScreen(navController, functionStr)
            }

            composable("scanDevice",) {
                ScanDeviceScreen(navController)
            }
        }
    }
}


@Composable
fun BottomBar(navController: NavController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val currentRoute = currentDestination?.route ?: ""

    val routes = remember { tabRowScreens.map { it.route } }





    if (currentRoute in routes) {
        BottomNavigation(backgroundColor = Color.White) {
            tabRowScreens.forEach { screen ->
                val selected =
                    currentDestination?.hierarchy?.any { it.route == screen.route } == true
                BottomNavigationItem(
                    selected = selected,
                    icon = {
                        Icon(
                            screen.icon!!,
                            contentDescription = null,
                            tint = if (selected) Color.Blue else Color.Gray
                        )
                    },
                    label = {
                        Text(
                            text = screen.route,
                            style = TextStyle(color = if (selected) Color.Blue else Color.Gray)
                        )
                    },
                    onClick = {
                        try {
                            navController.navigate(screen.route) {
                                restoreState = true
                                launchSingleTop = true
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    })
            }
        }
    }
}





@OptIn(ExperimentalPermissionsApi::class)
fun NavGraphBuilder.homeGraph(navController: NavHostController) {



    composable(SdkFunction.route) {

        val cameraPermissionState  =  rememberMultiplePermissionsState(permissions = arrayOf(
            // Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_MEDIA_LOCATION
        ).toList()
        )

        SdkFunction(
            chooseFunction = { functionStr ->
                navController.navigate("sendCommand/$functionStr")
            },
            scanDevice = {

                navController.navigate("scanDevice")
                if (cameraPermissionState.allPermissionsGranted){
                    navController.navigate("scanDevice")
                }else{
                    cameraPermissionState.launchMultiplePermissionRequest()
                }


            }
        )

    }
    composable(Dial.route) {
        DialScreen(navController)
    }


}




