package com.creek.dial.sportLive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportsLiveScreen(
    navController: NavHostController,
    viewModel: SportsLiveViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val jsonText by viewModel.jsonText.collectAsState()
    val sportTypes by viewModel.sportTypes.collectAsState()
    val currentType by viewModel.currentType.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sport Live") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Sport Type Picker
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = currentType.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Sport Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    sportTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name) },
                            onClick = {
                                viewModel.selectSportType(type)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(text = state, style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(16.dp))

            Text(
                text = jsonText.ifEmpty { "No live data yet..." },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(Modifier.height(24.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(modifier = Modifier.weight(1f), onClick = { viewModel.startSport() }) {
                    Text("Start")
                }
                Button(modifier = Modifier.weight(1f), onClick = { viewModel.pauseSport() }) {
                    Text("Pause")
                }
                Button(modifier = Modifier.weight(1f), onClick = { viewModel.resumeSport() }) {
                    Text("Resume")
                }
                Button(modifier = Modifier.weight(1f), onClick = { viewModel.endSport() }) {
                    Text("End")
                }
            }
        }
    }
}
