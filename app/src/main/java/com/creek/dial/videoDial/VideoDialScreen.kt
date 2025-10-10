@file:OptIn(ExperimentalMaterial3Api::class)

package com.creek.dial.videoDial

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.model.DialFunctionModel
import com.example.model.DialParseModel
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.creek.dial.customDial.BackgroundInfo
import com.creek.dial.customDial.ColorInfo

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun VideoDialScreen(
    navController: NavHostController,
    titleName: String,
    width: Int,
    height: Int,
    cornerRadius: Int
) {

    val context = LocalContext.current
    val viewModel: VideoDialViewModel = viewModel()
    val unZipCalled = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var videoUri by remember { mutableStateOf<Uri?>(null) }
    var cropRect by remember { mutableStateOf<android.graphics.Rect?>(null) }
    var firstFrameBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var cropImageUri by remember { mutableStateOf<Uri?>(null) }


    // uCrop 裁剪结果 launcher
    val cropLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val resultUri = UCrop.getOutput(result.data ?: return@rememberLauncherForActivityResult)
        if (result.resultCode == Activity.RESULT_OK && resultUri != null) {
            // 通过 Intent 的 Extra 获取裁剪参数
            val aspectRatio = result.data?.getFloatExtra(UCrop.EXTRA_OUTPUT_CROP_ASPECT_RATIO, 1f) ?: 1f
            val imageWidth = result.data?.getIntExtra(UCrop.EXTRA_OUTPUT_IMAGE_WIDTH, 0) ?: 0
            val imageHeight = result.data?.getIntExtra(UCrop.EXTRA_OUTPUT_IMAGE_HEIGHT, 0) ?: 0
            val offsetX = result.data?.getIntExtra(UCrop.EXTRA_OUTPUT_OFFSET_X, 0) ?: 0
            val offsetY = result.data?.getIntExtra(UCrop.EXTRA_OUTPUT_OFFSET_Y, 0) ?: 0

            // 构造 Rect 或自定义数据结构
            val cropInfo = android.graphics.Rect(offsetX, offsetY, offsetX + imageWidth, offsetY + imageHeight)
            cropRect = cropInfo
            viewModel.saveCropRect(cropInfo)
            // 可选：加载裁剪后的图片
            firstFrameBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, resultUri)
        }

    }

    // 视频选择器
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->

        if (uri != null) {
            viewModel.saveVideoToLocal(context, uri)
            coroutineScope.launch {
                val bitmap = viewModel.getFirstFrameFromVideo(context, uri)
                firstFrameBitmap = bitmap
                // 保存第一帧为临时文件
                if (bitmap != null) {
                    val tempFile = File(context.cacheDir, "frame_${System.currentTimeMillis()}.jpg")
                    FileOutputStream(tempFile).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    }
                    val sourceUri = Uri.fromFile(tempFile)
                    val destUri = Uri.fromFile(File(context.cacheDir, "crop_${System.currentTimeMillis()}.jpg"))
                    // 启动 uCrop
                    val uCrop = UCrop.of(sourceUri, destUri)
                        .withAspectRatio(width.toFloat(), height.toFloat())
                        .withMaxResultSize(width, height)
                    cropLauncher.launch(uCrop.getIntent(context))
                }
            }
        }
    }

    if (!unZipCalled.value) {
        viewModel.width = width
        viewModel.height = height
        viewModel.cornerRadius = cornerRadius
        viewModel.unzipFile(titleName = titleName, width, height, cornerRadius)
        unZipCalled.value = true
    }

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
                        "Centered Top App Bar",
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
                actions = {
                    TextButton(onClick = { viewModel.installDial() }) {
                        Text("Install", fontSize = 16.sp)
                    }
                },

                )
        }) { paddingValue ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValue)
                .verticalScroll(rememberScrollState())
                .background(color = Color(0xFFE7E8EA)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            Box(
                modifier = Modifier
                    .width((width / 3).dp)
                    .height((height / 3).dp)
                    .clip(RoundedCornerShape((cornerRadius / 3).dp))
                    .clickable {
                        // 点击选择视频
                        videoPickerLauncher.launch("video/*")
                    }
            ) {
                if (viewModel.uptateVideValue.intValue != 0) {
                    AndroidView(
                        factory = { ctx ->
                            VideoView(ctx).apply {
                                val file = File(viewModel.playVideoPath.value)
                                setVideoURI(Uri.fromFile(file))
                                setOnPreparedListener { it.isLooping = true }
                                start()
                            }
                        },
                        update = { view ->
                            // 更新状态时，复用 view，不重新创建
                            if(viewModel.uptateVideValue.intValue != 0){
                                view.stopPlayback()
                                val file = File(viewModel.playVideoPath.value)
                                view.setVideoURI(Uri.fromFile(file))
                                view.setOnPreparedListener { it.isLooping = true }
                                view.start()
                            }

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(width.toFloat() / height)
                    )
                    val bitmap = viewModel.loadImageFromBase64(viewModel.baseImage.value)
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "",
                            modifier = Modifier
                                .width((width / 3).dp)
                                .height((height / 3).dp)
                                .clip(shape = RoundedCornerShape((cornerRadius / 3).dp)),
                            contentScale = ContentScale.Fit
                        )

                    }
                } else {
                    // 修复资源ID为0时崩溃
                    val resId = context.resources.getIdentifier(
                        titleName,
                        "drawable",
                        context.packageName
                    )
                    if (resId != 0) {
                        Image(
                            painter = painterResource(id = resId),
                            contentDescription = "",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(width.toFloat() / height)
                                .clip(RoundedCornerShape((cornerRadius / 3).dp)),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        // 兜底显示一个灰色背景或占位图
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(width.toFloat() / height)
                                .clip(RoundedCornerShape((cornerRadius / 3).dp))
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No Preview", color = Color.DarkGray)
                        }
                    }
                }

            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp)
                        .background(color = Color.White)
                ) {
                    if ((viewModel.dialModel.value?.appColors ?: emptyList()).isNotEmpty()) {
                        ColorInfo(
                            dialList = viewModel.dialModel.value?.appColors ?: emptyList(),
                            selectIndex = viewModel.colorSelectedIndex.intValue
                        ) { index ->
                            viewModel.chooseColor(index)
                        }
                        Spacer(Modifier.size(20.dp))
                    }

                    if ((viewModel.dialModel.value?.clockPositionImagePaths ?: emptyList()).isNotEmpty()) {
                        var selectIndex = 0;
                        if ((viewModel.dialModel.value?.clockPositionImagePaths ?: emptyList()).isNotEmpty()) {
                            selectIndex = viewModel.dialModel.value?.clockPositionSelectIndexList!!.indexOf(
                                viewModel.dialModel.value?.videoSelectIndex ?: 0
                            )
                        }
                        BackgroundInfo(
                            dialList = viewModel.dialModel.value?.clockPositionImagePaths ?: emptyList(),
                            selectIndex = selectIndex,
                            width = width,
                            height = height,
                            cornerRadius = cornerRadius
                        ) { index ->
                            viewModel.choosePosition(index)
                        }
                    }
                }

            }
        }
    }

    @Composable
    fun ColorInfo(dialList: List<String>, selectIndex: Int, onClick: (Int) -> Unit) {
        Column {
            Text(text = "Color", fontWeight = FontWeight.Bold)
            Spacer(Modifier.size(10.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                itemsIndexed(dialList) { index, item ->
                    val borderColor = if (index == selectIndex) Color.Blue else Color.Gray
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(48.dp)
                            .background(
                                color = Color(android.graphics.Color.parseColor(item)),
                                shape = CircleShape
                            )
                            .border(2.dp, color = borderColor, shape = CircleShape)
                            .clickable { onClick(index) }
                    )
                }
            }
        }
    }

    @Composable
    fun BackgroundInfo(
        dialList: List<String>, selectIndex: Int, width: Int,
        height: Int,
        cornerRadius: Int, onClick: (Int) -> Unit
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(text = "Background", fontWeight = FontWeight.Bold)
            Spacer(Modifier.size(10.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                itemsIndexed(dialList) { index, item ->
                    val borderColor = if (index == selectIndex) Color.Black else Color.Transparent
                    Box(
                        modifier = Modifier
                            .width((width / 3).dp)
                            .height((height / 3).dp)
                            .clip(shape = RoundedCornerShape((cornerRadius / 3).dp))
                            .background(color = Color(0xFF799D9B), shape = RoundedCornerShape(20.dp))
                            .clickable { onClick(index) }
                    ) {

                        val file = File(item)
                        AsyncImage(
                            model = file,
                            contentDescription = "",
                            modifier = Modifier
                                .width((width / 3).dp)
                                .height((height / 3).dp)
                                .clip(shape = RoundedCornerShape((cornerRadius / 3).dp))
                                .border(
                                    3.dp,
                                    color = borderColor,
                                    shape = RoundedCornerShape((cornerRadius / 3).dp)
                                ),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
    }
}





