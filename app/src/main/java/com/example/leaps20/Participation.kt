package com.example.leaps20

import android.widget.Toast
import android.widget.Toast.makeText
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun ParticipationView(
    participation: ParticipationData = androidx.lifecycle.viewmodel.compose.viewModel(),
    navController: NavHostController
) {
    ParticipationHourView(
        participation,
        navController = navController
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipationHourView(
    participation: ParticipationData,
    navController: NavHostController
) {
    val uriHandler = LocalUriHandler.current
    val attendance by participation.attendance.collectAsState()

    var showEditDialog by remember { mutableStateOf(false) }
    var editedAttendance by remember { mutableStateOf(attendance.toString()) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        participation.fetchParticipation()
    }

    val levelDisplay = if (attendance == 0) "N/A" else participation.level
    val yearText = if (participation.year == 1) "year" else "years"

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFFB0E0E6), // Full blue background
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    SectionHeader(
                        title = "Participation",
                        subtitle = "Level: $levelDisplay",
                        icon = Icons.Default.Group,
                        navController = navController
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFB0E0E6)
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 180.dp, height = 200.dp)
                        .border(2.dp, Color.Black, shape = HexagonShape())
                        .shadow(
                            3.dp,
                            shape = HexagonShape(),
                            ambientColor = Color(0xFF505080),  // dark shadow
                            spotColor = Color(0xFF505080)
                        )
                        .background(Color(0xFF003366), shape = HexagonShape()) // Dark blue fill
                        .padding(16.dp)
                        .clickable {
                            editedAttendance = attendance.toString()
                            showEditDialog = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$attendance%",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "${participation.year} $yearText",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Level $levelDisplay",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        makeText(context, "Coming soon!", Toast.LENGTH_SHORT).show()
                    },
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(48.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Text(
                        "CCA attendance sheet",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }

    // Edit Attendance Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Participation") },
            text = {
                Column {
                    Text("Enter attendance percentage (0-100):")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = editedAttendance,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() } && newValue.length <= 3) {
                                editedAttendance = newValue
                            }
                        },
                        singleLine = true,
                        placeholder = { Text("e.g. 85") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newAttendanceInt = editedAttendance.toIntOrNull()
                        if (newAttendanceInt != null && newAttendanceInt in 0..100) {
                            participation.updateParticipation(newAttendanceInt)
                            showEditDialog = false
                        } else {
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

