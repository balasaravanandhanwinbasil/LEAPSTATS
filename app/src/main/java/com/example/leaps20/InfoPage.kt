package com.example.leaps20

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsCompat.Type
import androidx.lifecycle.viewmodel.compose.viewModel

val DarkBlue1 = Color(0xFF00497A)
val DarkerBlue1 = Color(0xFF003D6B)
val LightBlue1 = Color(0xFFCCE8FF)

@Composable
fun LEAPSApp(
    outerNavController: NavHostController,
    userData: UserData,
    leadershipData: LeadershipData,
    participationData: ParticipationData,
    achievementsData: AchievementsData,
    serviceData: ServiceData
) {
    val nestedNavController = rememberNavController()
    NavHost(navController = nestedNavController, startDestination = "info") {
        composable("info") {
            InfoView(
                onNavigate = { route -> nestedNavController.navigate(route) },
                navControllerForBack = outerNavController
            )
        }
        composable("leadership") { LeadershipInfoView(outerNavController) }
        composable("achievement") { AchievementInfoView(outerNavController) }
        composable("participation") { ParticipationInfoView(outerNavController) }
        composable("service") { ServiceInfoView(outerNavController) }
        composable("attainment") {
            AttainmentView(
                navController = outerNavController,
                userData = userData,
                leadershipData = leadershipData,
                participationData = participationData,
                achievementsData = achievementsData,
                serviceData = serviceData
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithBackButton(title: String, onBack: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    )
}


@Composable
fun InfoView(
    onNavigate: (String) -> Unit,
    navControllerForBack: NavHostController  // <-- renamed param to make clear this is for back
) {
    val view = LocalView.current
    val windowInsets = ViewCompat.getRootWindowInsets(view)
    val statusBarHeightPx = windowInsets?.getInsets(Type.statusBars())?.top ?: 0
    val statusBarHeightDp = with(LocalDensity.current) { statusBarHeightPx.toDp() }
    val DarkerAchievementBlue = Color(0xFF1A5DA8)

    Scaffold(topBar = {
        TopAppBarWithBackButton(title = "LEAPS INFO") {
            // Use outer nav controller here for popping back
            navControllerForBack.popBackStack()
        }
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(top = statusBarHeightDp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoCard("Leadership", Icons.Default.Person, DarkBlue1, MaterialTheme.colorScheme.onPrimary) { onNavigate("leadership") }
                InfoCard("Achievement", Icons.Default.EmojiEvents, DarkerAchievementBlue.copy(alpha = 0.8f), MaterialTheme.colorScheme.onPrimary) { onNavigate("achievement") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoCard("Participation", Icons.Default.Group, DarkerBlue1, MaterialTheme.colorScheme.onPrimary) { onNavigate("participation") }
                InfoCard("Service", Icons.Default.Autorenew, Color(0xFF034A9E), MaterialTheme.colorScheme.onPrimary) { onNavigate("service") }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .width(330.dp)
                    .height(120.dp)
                    .background(Color(0xFFE0FFFF), shape = MaterialTheme.shapes.medium) // Light cyan background
                    .clickable { onNavigate("attainment") },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Level of Attainment",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        Icons.Default.Grade,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun InfoCard(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .size(160.dp)
            .background(backgroundColor, shape = MaterialTheme.shapes.medium)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(label, color = textColor, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(48.dp))
    }
}

@Composable
fun LeadershipInfoView(navController: NavHostController) {
    val levels = listOf(
        "Level 0:\n- No roles",
        "Level 1:\n- Completed 2 leadership modules (at least 3h each)",
        "Level 2:\n- Class Committee\n- Committee for student-initiate/student-led projects, approved by school\n- NYAA Bronze",
        "Level 3:\n- Class Chairperson\n- Lower Sec Student Council\n- Lower Sec Peer Support Leader\n- Lower Sec ACE Leader\n- Lower Sec House Leader\n- Lower Sec DC leader\n- Lower Sec CCA Exco Committee\n- Exco for school-wide events\n- Chairperson/Vice-Chairperson for student-initiated/student-led projects, approved by school\n- NYAA Silver/Gold",
        "Level 4:\n- Upper Sec Student Council\n- Upper Sec Peer Support Leader\n- Upper Sec ACE Leader\n- Upper Sec House Exco\n- Upper Sec House Captain\n- Upper Sec House Vice-Captain\n- Upper Sec DC leader\n- Chairperson/ViceChair person for school-wide events\n- Upper Sec CCA Exco Committee",
        "Level 5:\n- Student Council Exco\n- Peer Support Leader Exco\n- ACE Leader Exco\n- House Leader Exco\n- DC leader Exco\n- CCA Chairperson/Vice-Chairperson"
    )
    val pairs = levels.map {
        val i = it.indexOf(':')
        if (i != -1) it.substring(0, i + 1) to it.substring(i + 1).trim() else it to ""
    }
    InfoDetailScreen("Leadership", DarkBlue1.copy(alpha = 0.5f), pairs) { navController.popBackStack() }
}

@Composable
fun AchievementInfoView(navController: NavHostController) {
    val list = listOf(
        "Level 0:" to "Not enough achievements",
        "Level 1:" to "Represented class or CCA at intraschool event.",
        "Level 2:" to "Represented school at a local event for 1 year.",
        "Level 3:" to "Represented school/external at local/international event for 1 year and received:\n1. Top 4 team placing\n2. Top 8 individual\nRepresented school/external at local/international event for 2 years",
        "Level 4:" to "Represented school/external at local/international event for 3-4 years\nRepresented UG HQ at international event\nRepresented school/external at local/international event for 2 years and received:\n1. Top 4 team placing\n2. Top 8 individual",
        "Level 5:" to "Represented school at local/international competition\nRepresented Singapore at international event, approved by Singapore organisation\nRepresented National Project of Excellence at local/international event\nRepresented MOE at local/international event\nRepresented UG HQ at international competition AND Represented Singapore Schools/National Project of Excellence/MOE at local/international event\nRepresented Singapore at international event, approved by Singapore organisation\nSYF Arts Presentation"
    )
    InfoDetailScreen("Achievement", LightBlue1.copy(alpha = 0.6f), list) { navController.popBackStack() }
}

@Composable
fun ParticipationInfoView(navController: NavHostController) {
    val list = listOf(
        "Level 0:" to "Did not participate in CCA enough",
        "Level 1:" to "Participated in any CCA for 2 years with at least 75% attendance each year.",
        "Level 2:" to "Participated in any CCA for 3 years with at least 75% attendance each year.",
        "Level 3:" to "Participated in any CCA for 4 years with at least 75% attendance each year.",
        "Level 4:" to "Participated in the same CCA for 4 years with exemplary conduct.",
        "Level 5:" to "Participated in the same CCA for 5 years with exemplary conduct."
    )
    InfoDetailScreen("Participation", DarkerBlue1.copy(alpha = 0.6f), list) { navController.popBackStack() }
}

@Composable
fun ServiceInfoView(navController: NavHostController) {
    val list = listOf(
        "Level 0:" to "Less than 24 hours",
        "Level 1:" to "24 to 30 service hours",
        "Level 2:" to "30 to 36 service hours\nCompleted at least 1 VIA project for the school/community",
        "Level 3:" to "At least 36 service hours\nCompleted 2 VIA projects for the school/community\n24 service hours and 1 VIA project for the school/community",
        "Level 4:" to "24 service hours and 2 VIA projects for the school/community",
        "Level 5:" to "24 hours of service and 1 student-led project for the community and 1 VIA project."
    )
    InfoDetailScreen("Service", Color(0xFF034A9E).copy(alpha = 0.6f), list) { navController.popBackStack() }
}

@Composable
fun AttainmentView(
    navController: NavHostController,
    userData: UserData,
    leadershipData: LeadershipData = viewModel(),
    serviceData: ServiceData = viewModel(factory = ServiceDataFactory(LocalContext.current.applicationContext as Application)),
    participationData: ParticipationData = viewModel(),
    achievementsData: AchievementsData = viewModel()
) {
    var attainmentState by remember { mutableStateOf("Fair") }
    var points by remember { mutableStateOf(0) }
    var attainmentColor by remember { mutableStateOf(Color.Red.copy(alpha = 0.5f)) }

    LaunchedEffect(
        leadershipData.currentLeadershipPosition.collectAsState(initial = "").value,
        achievementsData.currentHighestLevel.collectAsState(initial = 0).value,
        participationData.attendance.collectAsState(initial = 0).value,
        serviceData.level.collectAsState(initial = 0).value
    ) {
        attainmentState = userData.attainment(
            leadershipData,
            serviceData,
            participationData,
            achievementsData
        )
        points = when (attainmentState) {
            "Excellent" -> 2
            "Good" -> 1
            else -> 0
        }
        attainmentColor = when (attainmentState) {
            "Excellent" -> Color.Green.copy(alpha = 0.6f)
            "Good" -> Color(0xFFFFA500) // orange
            else -> Color.Red.copy(alpha = 0.5f)
        }
    }

    val attainmentLevels = listOf(
        "1. Excellent:\nAt least Level 3 in all domains, and Level 4 or higher in at least one domain.",
        "2. Good:\nAt least Level 1 in all domains, and Level 2 or higher in at least three domains.",
        "3. Fair:\nDoes not meet the above criteria."
    )

    Scaffold(
        topBar = {
            TopAppBarWithBackButton("Level of Attainment") {
                navController.popBackStack()
            }
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                "$attainmentState: $points Point${if (points == 1) "" else "s"}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = attainmentColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(attainmentLevels) {
                    LevelCard(it.substringBefore(":"), it.substringAfter(":").trim())
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoDetailScreen(
    title: String,
    backgroundColor: Color,
    levels: List<Pair<String, String>>,
    onBack: () -> Unit
) {
    Scaffold(topBar = {
        TopAppBarWithBackButton(title, onBack)
    }) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                "What is $title?",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    when (title) {
                        "Achievement" -> "Achievement levels are for students accomplishments in CCA..."
                        "Participation" -> "Participation is for students participation in CCA..."
                        "Service" -> "Service hours are for students who contribute to the community..."
                        "Leadership" -> "Leadership levels are for students who develop leadership skills..."
                        else -> ""
                    },
                    fontSize = 16.sp,
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(levels) { (title, desc) ->
                    LevelCard(title, desc)
                }
            }
        }
    }
}

@Composable
fun LevelCard(title: String, description: String) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
