package com.codex.leapSTATS.TeacherView

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentView() {
    val teacherName = "RUTH LOH"
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    // Show back button only when not on main screen
                    val currentRoute = navController.currentBackStackEntry?.destination?.route
                    if (currentRoute != "cca_view") {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable {
                                navController.navigate("teacher_profile")
                            }
                    ) {
                        Text(
                            text = teacherName,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "cca_view",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("cca_view") {
                CCAView(navController = navController)
            }
            composable("cca_detail/{ccaTitle}") { backStackEntry ->
                val ccaTitle = backStackEntry.arguments?.getString("ccaTitle") ?: ""
                val item = createHexagonItems().find { it.title == ccaTitle }
                if (item != null) {
                    CcaDetailView(item = item, navController = navController)
                }
            }
            composable("teacher_profile") {
                TeacherProfileView(navController = navController, teacherName = teacherName)
            }
        }
    }
}

@Composable
fun CCAView(navController: NavHostController) {
    val items = createHexagonItems()

    // Make the entire view scrollable
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .horizontalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .width(1100.dp)  // Increased width to accommodate all hexagons
                .height(700.dp)  // Increased height
                .padding(50.dp)  // Add padding for better visibility
        ) {
            items.forEach { item ->
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                x = item.x.dp.roundToPx(),
                                y = item.y.dp.roundToPx()
                            )
                        }
                        .clickable {
                            navController.navigate("cca_detail/${item.title}")
                        }
                ) {
                    HexagonView(
                        text = item.title,
                        color = colorForSection(item.section),
                        fontSize = item.fontSize
                    )
                }
            }
        }
    }
}

@Composable
fun TeacherProfileView(navController: NavHostController, teacherName: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp)
    ) {
        Text(
            text = "Profile",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )



        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Name: $teacherName",
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Role: CCA Teacher",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Email: ${teacherName.lowercase().replace(" ", "_")}@sst.edu.sg",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

fun createHexagonItems(): List<HexagonItem> {
    return listOf(
        // Left section - Red
        HexagonItem(title = "Astronomy Club", x = 100f, y = 200f, isCenter = false, section = HexagonItem.Section.LEFT, fontSize = 14f),
        HexagonItem(title = "Guitar Ensemble", x = 100f, y = 360f, isCenter = false, section = HexagonItem.Section.LEFT, fontSize = 14f),
        HexagonItem(title = "ARC @SST", x = 230f, y = 120f, isCenter = false, section = HexagonItem.Section.LEFT, fontSize = 14f),
        HexagonItem(title = "Floorball", x = 230f, y = 280f, isCenter = false, section = HexagonItem.Section.LEFT, fontSize = 14f),
        HexagonItem(title = "Media Club", x = 230f, y = 440f, isCenter = false, section = HexagonItem.Section.LEFT, fontSize = 14f),

        // Middle section - Blue
        HexagonItem(title = "Athletics (Track)", x = 360f, y = 200f, isCenter = false, section = HexagonItem.Section.MIDDLE, fontSize = 14f),
        HexagonItem(title = "Robotics @APEX", x = 360f, y = 360f, isCenter = false, section = HexagonItem.Section.MIDDLE, fontSize = 14f),
        HexagonItem(title = "Badminton", x = 490f, y = 120f, isCenter = false, section = HexagonItem.Section.MIDDLE, fontSize = 14f),
        HexagonItem(title = "SST CCA", x = 490f, y = 280f, isCenter = true, section = HexagonItem.Section.MIDDLE, fontSize = 25f),
        HexagonItem(title = "Scouts", x = 490f, y = 440f, isCenter = false, section = HexagonItem.Section.MIDDLE, fontSize = 14f),
        HexagonItem(title = "Basketball", x = 620f, y = 200f, isCenter = false, section = HexagonItem.Section.MIDDLE, fontSize = 14f),
        HexagonItem(title = "Show Choir and Dance", x = 620f, y = 360f, isCenter = false, section = HexagonItem.Section.MIDDLE, fontSize = 14f),

        // Right section - Gray
        HexagonItem(title = "English Drama Club", x = 750f, y = 120f, isCenter = false, section = HexagonItem.Section.RIGHT, fontSize = 14f),
        HexagonItem(title = "Football", x = 750f, y = 280f, isCenter = false, section = HexagonItem.Section.RIGHT, fontSize = 14f),
        HexagonItem(title = "SYFC", x = 750f, y = 440f, isCenter = false, section = HexagonItem.Section.RIGHT, fontSize = 14f),
        HexagonItem(title = "Fencing", x = 880f, y = 200f, isCenter = false, section = HexagonItem.Section.RIGHT, fontSize = 14f),
        HexagonItem(title = "Taekwondo", x = 880f, y = 360f, isCenter = false, section = HexagonItem.Section.RIGHT, fontSize = 14f)
    )
}

fun colorForSection(section: HexagonItem.Section): Color {
    return when (section) {
        HexagonItem.Section.LEFT -> Color.Red
        HexagonItem.Section.MIDDLE -> Color.Blue
        HexagonItem.Section.RIGHT -> Color.Gray
    }
}