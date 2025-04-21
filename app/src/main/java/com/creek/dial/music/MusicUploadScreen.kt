package com.creek.dial.music

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType.Companion.Sp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.mylibrary.CreekManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicUploadScreen(navController: NavHostController) {
    val viewModel: MusicUploadViewModel = viewModel() // 获取 ViewModel
    val context = LocalContext.current

    // 获取 ViewModel 中的状态
    val singerName = viewModel.singerName.value
    val albumName = viewModel.albumName.value
    val selectedFile = viewModel.selectedFile.value

    // 使用 ActivityResultContracts 处理文件选择
    val openFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                // 调用 ViewModel 来处理选中的文件
                viewModel.onFileSelected(it)
            }
        }
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
                        "upload music",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)
                .padding(16.dp),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // 选择文件按钮
            Button(onClick = {
                // 调用文件选择器
                openFileLauncher.launch("audio/*") // 只允许选择音频文件
            }) {
                Text("select file")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 展示歌手名
            Text(
                text = "Singer: $singerName",
                style = TextStyle(fontSize = TextUnit(value = 20f, type = Sp)) // 显示歌手名
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 展示专辑名
            Text(
                text = "Album: $albumName",
                style = TextStyle(fontSize = TextUnit(value = 20f, type = Sp)) // 显示专辑名
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 开始上传按钮
            Button(onClick = { viewModel.startUpload() }) {
                Text("start upload")
            }
        }


    }


}