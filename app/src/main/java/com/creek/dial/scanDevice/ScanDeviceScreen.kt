@file:OptIn(ExperimentalMaterial3Api::class)

package com.creek.dial.scanDevice

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mylibrary.ConnectionStatus
import com.example.mylibrary.CreekManager
import com.example.mylibrary.CreekPlugin

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScanDeviceScreen(navController: NavHostController) {



    val viewmodel: ScanDeviceViewModel = viewModel()


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = WindowInsets(top = 0.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                title = { Text("Scan Device") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
            )
        }) { paddingValue ->

        LazyColumn(
            modifier = Modifier
                .padding(paddingValue)
                .background(color = Color.White)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(10.dp)
        ) {

            items(viewmodel.deviceList) { deviceInfo ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                ) {
                    Column {
                        deviceInfo.device?.name?.let { Text(text = it) }
                        (deviceInfo?.macAddress ?: deviceInfo.device?.id)?.let { Text(text = it) }
                    }
                    Button(onClick = { viewmodel.connect(deviceInfo.device?.id ?: "") }) {
                        if (viewmodel.connectDeviceId.value  != ""){
                            if ((viewmodel.connectDeviceId.value == deviceInfo.device?.id ?: "") && CreekPlugin.sInstance.connectStatus == ConnectionStatus.CONNECT){
                                Text(text = "disconnect", color = Color.Red)
                            }else{
                                Text(text = "connect")
                            }
                        }else{
                            Text(text = "connect")
                        }

                    }
                }
                HorizontalDivider(thickness = 0.3.dp, color = Color.Gray)
            }
        }
    }
}