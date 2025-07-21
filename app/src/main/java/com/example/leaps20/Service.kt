package com.example.leaps20

import AddEventHexagon
import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


@Composable
fun ServiceHoursView(
    serviceData: ServiceData,
    navController: NavHostController
) {
    val events by serviceData.hexes.collectAsState()
    var showSheet by remember { mutableStateOf(false) }
    var eventName by remember { mutableStateOf("") }
    var hours by remember { mutableStateOf(0) }
    var selectedService by remember { mutableStateOf("Others") }
    var editingIndex by remember { mutableStateOf<Int?>(null) }
    val level by serviceData.level.collectAsState(initial = "0")

    val serviceTypes = listOf("Others", "VIA for school/community", "SIP for school/community")

    fun resetForm() {
        eventName = ""
        hours = 0
        selectedService = "Others"
        editingIndex = null
        showSheet = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SectionHeader(
                title = "Service",
                subtitle = "Level: $level",
                icon = Icons.Default.Autorenew,
                navController = navController
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy((-18).dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.offset(x = (-60).dp, y = 16.dp)
                ) {
                    events.filterIndexed { index, _ -> index % 2 == 0 }
                        .forEachIndexed { colIndex, event ->
                            ServiceHexagon(
                                name = event.name,
                                hours = event.hours,
                                modifier = Modifier
                                    .size(150.dp, 160.dp)
                                    .clickable {
                                        eventName = event.name
                                        hours = event.hours
                                        selectedService = event.type
                                        editingIndex = colIndex * 2
                                        showSheet = true
                                    }
                                    .border(2.dp, Color.Black, shape = HexagonShape())
                            )
                        }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy((-18).dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.offset(x = 60.dp, y = 86.dp)
                ) {
                    events.filterIndexed { index, _ -> index % 2 == 1 }
                        .forEachIndexed { colIndex, event ->
                            ServiceHexagon(
                                name = event.name,
                                hours = event.hours,
                                modifier = Modifier
                                    .size(150.dp, 160.dp)
                                    .clickable {
                                        eventName = event.name
                                        hours = event.hours
                                        selectedService = event.type
                                        editingIndex = colIndex * 2 + 1
                                        showSheet = true
                                    }
                                    .border(2.dp, Color.Black, shape = HexagonShape())
                            )
                        }
                }
            }

            Spacer(modifier = Modifier.height(65.dp))

            AddAchievementHexagon(
                onClick = {
                    resetForm()
                    showSheet = true
                }
            )

            if (showSheet) {
                AlertDialog(
                    onDismissRequest = { showSheet = false },
                    title = { Text(if (editingIndex != null) "Edit Service" else "Add Service") },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = eventName,
                                onValueChange = { eventName = it },
                                label = { Text("Event Name*") }, // mark required
                                isError = eventName.isBlank()
                            )
                            if (eventName.isBlank()) {
                                Text(
                                    text = "Event name cannot be empty",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("$hours hour${if (hours == 1) "" else "s"}")
                                Spacer(modifier = Modifier.width(12.dp))
                                Slider(
                                    value = hours.toFloat(),
                                    onValueChange = { hours = it.toInt() },
                                    valueRange = 0f..100f,
                                    steps = 99
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            DropdownWithHeader(
                                label = "Service Type",
                                options = serviceTypes,
                                selectedOption = selectedService,
                                onOptionSelected = { selectedService = it }
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (eventName.isNotBlank()) {
                                    showSheet = false // Close the sheet immediately

                                    if (editingIndex != null) {
                                        serviceData.updateServiceEvent(
                                            editingIndex!!,
                                            eventName,
                                            hours,
                                            selectedService
                                        ) { success ->
                                            if (success) resetForm()
                                        }
                                    } else {
                                        serviceData.addServiceEvent(eventName, hours, selectedService) { success ->
                                            if (success) resetForm()
                                        }
                                    }
                                    serviceData.updateLevel()
                                }
                            },
                            enabled = eventName.isNotBlank()
                        ) {
                            Text(if (editingIndex != null) "Update" else "Add")
                        }
                    }
                    ,
                    dismissButton = {
                        if (editingIndex != null) {
                            TextButton(
                                onClick = {
                                    showSheet = false // Close immediately
                                    serviceData.removeServiceEvent(editingIndex!!) { success ->
                                        if (success) resetForm()
                                    }
                                }
                            ) {
                                Text("Delete", color = Color.Red)
                            }
                        }
                        TextButton(onClick = { showSheet = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ServiceHexagon(name: String, hours: Int, modifier: Modifier = Modifier) {
    HexagonShapeBox(
        // Much lighter green colors:
        color = if (hours < 3) Color(0xFFD6EDEA) else Color(0xFFB7E4DB),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 3,
                softWrap = true,
                overflow = TextOverflow.Ellipsis,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "$hours hour${if (hours == 1) "" else "s"}",
                fontSize = 12.sp,
                color = Color.Black
            )
        }
    }
}
