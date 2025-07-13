package com.creek.dial.sendCommand

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mylibrary.CreekManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SendCommandScreen(navController: NavHostController, functionStr: String) {

    val viewModel = SendCommandViewModel()
    val context = LocalContext.current
    val contactsPermissionState = rememberPermissionState(permission = Manifest.permission.READ_CONTACTS)
    val calendarPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                windowInsets = WindowInsets(top = 0.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        functionStr,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
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
        Box(modifier = Modifier.padding(paddingValue)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White)
                    .padding(16.dp),

                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    Button(onClick = {
                        if(functionStr == "requst contacts permission"){
                            contactsPermissionState.launchPermissionRequest()
                        }else{
                            viewModel.sendCommand(functionStr, context = context)
                        }
                    }) {
                        Text("Send Command")
                    }
                    when {
                        contactsPermissionState.status.isGranted -> {
                            Log.w("authorize", "User agrees to authorize")
                            CreekManager.sInstance.monitorPhone()
                        }

                    else -> {

                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    enabled = false,
                    shape = RoundedCornerShape(corner = CornerSize(5.dp)),
                    value = viewModel.responseText.value,
                    onValueChange = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .weight(1f),

                    )
            }
        }
        if (viewModel.loddingState.value){
            Box(
                modifier = Modifier.fillMaxSize(), // 填充全屏
                contentAlignment = Alignment.Center // 子内容居中
            ) {
                Surface(
                    modifier = Modifier.wrapContentSize(),
                    color = Color.Gray
                ) {
                    CircularProgressIndicator()
                }
            }
        }


    }

}