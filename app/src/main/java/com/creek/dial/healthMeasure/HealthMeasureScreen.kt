package com.creek.dial.healthMeasure
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.proto.Enums
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthMeasureScreen(
    navController: NavHostController? = null,
    viewModel: HealthMeasureViewModel = viewModel()
) {
    val types = listOf(
        Enums.ring_health_type.RING_HEART_RATE to "心率",
        Enums.ring_health_type.RING_STRESS to "压力",
        Enums.ring_health_type.RING_SPO2 to "血氧",
        Enums.ring_health_type.RING_HRV to "HRV",
        Enums.ring_health_type.RING_RESPIRATORY_RATE to "呼吸率",
        Enums.ring_health_type.AF to "房颤检测"
    )

    val selectedType = viewModel.selectedType
    val statusText = viewModel.statusText
    val resultText = viewModel.resultText

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("健康测量") },
                navigationIcon = {
                    navController?.let {
                        IconButton(onClick = { it.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // 下拉选择器（代替 UIPickerView）
            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    readOnly = true,
                    value = types.first { it.first == selectedType }.second,
                    onValueChange = {},
                    label = { Text("测量类型") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    types.forEach { (type, name) ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                viewModel.selectedType = type
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 开始按钮
            Button(
                onClick = { viewModel.startMeasure() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007AFF)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("开始测量", fontSize = 18.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(15.dp))

            // 停止按钮
            Button(
                onClick = { viewModel.stopMeasure() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("停止测量", fontSize = 18.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 状态显示
            Text(
                text = statusText,
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 结果显示
            Text(
                text = resultText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}