package com.creek.dial.dial

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.unit.sp
import com.creek.dial.navigateToVideoDialScreen
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DialScreen(navController: NavHostController) {

    val viewModel = DialViewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFE7E8EA)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 顶部栏，右上角按钮
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Dial",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            actions = {
                TextButton(onClick = {
                    // 跳转到视频表盘页面，参数可根据实际需求调整
                    navController.navigateToVideoDialScreen(
                        titleName = "video",
                        width = 466,
                        height = 466,
                        cornerRadius = 233
                    )
                }) {
                    Text("video")
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
        )

        Spacer(Modifier.size(15.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            SquareDial(
                dialList = viewModel.squareDialList,
                selectIndex = viewModel.squareDialSelected.intValue,
                onClick = { index -> viewModel.chooseSquareDial(navController, index) }
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            CircleDial(
                dialList = viewModel.circleDialList,
                selectIndex = viewModel.circleDialSelected.intValue,
                onClick = { index -> viewModel.chooseCircleDial(navController,index) }
            )
        }
    }
}

@Composable
fun CircleDial(dialList: List<String>, selectIndex: Int, onClick: (Int) -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        Text(text = "CircleDial", fontWeight = FontWeight.Bold)
        Spacer(Modifier.size(10.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            itemsIndexed(dialList) { index, item ->
                val borderColor = if (index == selectIndex) Color.Black else Color.Gray
                Box(
                    modifier = Modifier
                        .width((466/3).dp)
                        .height((466/3).dp)
                        .background(color = Color.Transparent, shape = CircleShape)
                        .border(2.dp, color = borderColor, shape = CircleShape)
                        .clickable { onClick(index) }
                ){
                    Image(
                        painter = painterResource(id = context.resources.getIdentifier(item, "drawable", context.packageName)),
                        contentDescription = null, // 这里需要提供内容描述，但如果您不需要，可以设置为null
                        modifier = Modifier.fillMaxSize(), // 使用fillMaxSize使图片填充整个Box
                        contentScale = ContentScale.Fit // 根据需要调整内容缩放模式
                    )
                }
            }
        }
    }
}

@Composable
fun SquareDial(dialList: List<String>, selectIndex: Int, onClick: (Int) -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
    ) {
        Text(text = "SquareDial", fontWeight = FontWeight.Bold)
        Spacer(Modifier.size(10.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            itemsIndexed(dialList) { index, item ->
                val borderColor = if (index == selectIndex) Color.Black else Color.Gray
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape((60/3).dp))
                        .width((368/3).dp)
                        .height((448/3).dp)
                        .background(color = Color(0xFF799D9B), shape = RoundedCornerShape((60/3).dp))
                        .border(2.dp, color = borderColor, shape = RoundedCornerShape((60/3).dp))
                        .clickable { onClick(index) }
                ){
                    Image(
                        painter = painterResource(id = context.resources.getIdentifier(item, "drawable", context.packageName)),
                        contentDescription = null, // 这里需要提供内容描述，但如果您不需要，可以设置为null
                        modifier = Modifier.fillMaxSize(), // 使用fillMaxSize使图片填充整个Box
                        contentScale = ContentScale.Fit // 根据需要调整内容缩放模式
                    )
                }
            }
        }
    }


}