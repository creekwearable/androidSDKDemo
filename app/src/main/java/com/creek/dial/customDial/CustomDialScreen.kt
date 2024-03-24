@file:OptIn(ExperimentalMaterial3Api::class)

package com.creek.dial.customDial

import android.annotation.SuppressLint
import android.os.Build
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
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.model.DialFunctionModel
import com.example.model.DialParseModel
import java.io.File

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CustomDialScreen(
    navController: NavHostController,
    titleName: String,
    width: Int,
    height: Int,
    cornerRadius: Int
) {

    val context = LocalContext.current
    val viewModel: CustomDialViewModel = viewModel()
    val unZipCalled = remember { mutableStateOf(false) }
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

            } else {
                Image(
                    painter = painterResource(
                        id = context.resources.getIdentifier(
                            titleName,
                            "drawable",
                            context.packageName
                        )
                    ),
                    contentDescription = "",
                    modifier = Modifier
                        .width((width / 3).dp)
                        .height((height / 3).dp)
                        .clip(shape = RoundedCornerShape((cornerRadius / 3).dp)),
                    contentScale = ContentScale.Fit
                )
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
                    if ((viewModel.dialModel.value?.appColors ?: emptyList()).isNotEmpty()){
                        ColorInfo(
                            dialList = viewModel.dialModel.value?.appColors ?: emptyList(),
                            selectIndex = viewModel.colorSelectedIndex.intValue
                        ) { index ->
                            viewModel.chooseColor(index)
                        }
                        Spacer(Modifier.size(20.dp))
                    }
                    if ((viewModel.dialModel.value?.backgroundImagePaths ?: emptyList()).isNotEmpty()){
                        BackgroundInfo(
                            dialList = viewModel.dialModel.value?.backgroundImagePaths ?: emptyList(),
                            selectIndex = viewModel.backgroundSelectedIndex.intValue,
                            width = width,
                            height = height,
                            cornerRadius = cornerRadius
                        ) { index ->
                            viewModel.chooseBackground(index)
                        }
                    }
                }
            }
            if ((viewModel.dialModel.value?.functions ?: emptyList()).isNotEmpty()){
                CustomFunctionality(
                    positionSelectedIndex = viewModel.positionSelectedIndex.intValue,
                    functionSelectedIndex = viewModel.functionSelectedIndex.intValue,
                    functionList = viewModel.dialModel.value?.functions ?: emptyList(),
                    onChoosePosition = { index -> viewModel.choosePosition(index) },
                    onChooseFunction = { index -> viewModel.chooseFunction(index) },
                    viewModel = viewModel
                ) {
                    viewModel.customDial()
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
                            color = Color(android.graphics.Color.parseColor("$item")),
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
fun BackgroundInfo(dialList: List<String>, selectIndex: Int, width: Int,
                   height: Int,
                   cornerRadius: Int, onClick: (Int) -> Unit) {
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
                ){

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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomFunctionality(
    positionSelectedIndex: Int,
    functionSelectedIndex: Int,
    functionList: List<DialFunctionModel>,
    onChoosePosition: (Int) -> Unit,
    onChooseFunction: (Int) -> Unit,
    viewModel: CustomDialViewModel,
    onConfirm: () -> Unit
) {

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clickable {
                showBottomSheet = true
            },
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween
        ) {
            Text(text = "Custom Functionality", fontWeight = FontWeight.Bold)
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Black,
            )
        }
    }

    if (showBottomSheet) {

        val configuration = LocalConfiguration.current
        val screenHeightDp = configuration.screenHeightDp

        ModalBottomSheet(
            containerColor = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .height((screenHeightDp * 0.9).dp),
            onDismissRequest = {
                onConfirm()
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            FunctionalityInfo(
                positionSelectedIndex = positionSelectedIndex,
                functionSelectedIndex = functionSelectedIndex,
                functionList = functionList,
                onChoosePosition = onChoosePosition,
                onChooseFunction = onChooseFunction,
                viewModel = viewModel
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FunctionalityInfo(
    positionSelectedIndex: Int,
    functionSelectedIndex: Int,
    functionList: List<DialFunctionModel>,
    onChoosePosition: (Int) -> Unit,
    onChooseFunction: (Int) -> Unit,
    viewModel: CustomDialViewModel
) {

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .background(color = Color(0xFFE7E8EA), shape = RoundedCornerShape(20.dp))
        ) {
            LazyRow(
                modifier = Modifier.padding(vertical = 15.dp, horizontal = 8.dp),
                contentPadding = PaddingValues(horizontal = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                itemsIndexed(functionList) { index, item ->
                    val borderColor =
                        if (index == positionSelectedIndex) Color.Black else Color.Gray
                    Box(
                        modifier = Modifier
                            .width((viewModel.width / 3).dp)
                            .height((viewModel.height / 3).dp)
                            .background(
                                color = Color(0xFF799D9B),
                                shape = RoundedCornerShape((viewModel.cornerRadius / 3).dp)
                            )
                            .border(
                                2.dp,
                                color = borderColor,
                                shape = RoundedCornerShape((viewModel.cornerRadius / 3).dp)
                            )
                            .clickable { onChoosePosition(index) }
                    ){
                        val bitmap = viewModel.loadImageFromBase64( item.positionImage ?: "")
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "",
                                modifier = Modifier
                                    .width((viewModel.width / 3).dp)
                                    .height((viewModel.height / 3).dp)
                                    .clip(shape = RoundedCornerShape((viewModel.cornerRadius / 3).dp)),
                                contentScale = ContentScale.Fit
                            )

                        }
                    }

                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
        ) {
            itemsIndexed(functionList[viewModel.positionSelectedIndex.value].typeModels ?: emptyList()) { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .padding(start = 10.dp, end = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    val file = File(item.image ?: "")
                    AsyncImage(
                        model = file,
                        contentDescription = "",
                        modifier = Modifier
                            .width(40.dp)
                            .height(40.dp)
                            .background(color = Color.Gray)
                            .clip(shape = RoundedCornerShape(20.dp)),

//                            .border(3.dp, color = Color.Transparent, shape = RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = item.type ?: "",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(weight = 1F, fill = true)
                    )
                    CircularCheckbox(checked = index == functionList[viewModel.positionSelectedIndex.value].selectedIndex) {
                        onChooseFunction(index)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun CircularCheckbox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .background(
                color = if (checked) Color.Blue else Color.Gray,
                shape = CircleShape
            )
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}