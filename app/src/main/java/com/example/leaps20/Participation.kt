package com.example.leaps20

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
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
                            ambientColor = Color(0xFFB0B0B0),
                            spotColor = Color(0xFFB0B0B0)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$attendance%",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "${participation.year} $yearText",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Level $levelDisplay",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        uriHandler.openUri("https://docs.google.com/spreadsheets/u/0/?tgif=d")
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
}
