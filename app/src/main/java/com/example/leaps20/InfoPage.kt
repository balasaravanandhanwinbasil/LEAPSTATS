package com.example.leaps20

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.core.view.WindowInsetsCompat.Type
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.leaps20.ui.theme.LeapsBlue

val DarkBlue1 = Color(0xFF00497A)
val DarkerBlue1 = Color(0xFF003D6B)
val LightBlue1 = Color(0xFF5C7ACC)

val backgroundColour = Color(0xFFB0E0E6).copy(alpha = 0.5f)

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
fun TopAppBarWithBackButton(
    title: String,
    onBack: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Row{
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = "icon",
                    tint = LeapsBlue,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Colours.background
        )
    )
}


@Composable
fun InfoView(
    onNavigate: (String) -> Unit,
    navControllerForBack: NavHostController
) {
    val view = LocalView.current
    val windowInsets = ViewCompat.getRootWindowInsets(view)
    val statusBarHeightPx = windowInsets?.getInsets(Type.statusBars())?.top ?: 0
    val statusBarHeightDp = with(LocalDensity.current) { statusBarHeightPx.toDp() }
    val DarkerAchievementBlue = Color(0xFF1A5DA8)

    Scaffold(topBar = {
        TopAppBarWithBackButton(title = "LEAPS INFO") {
            navControllerForBack.popBackStack()
        }
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(top = statusBarHeightDp)
                .fillMaxSize()
                .background(Colours.background),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoCard("Leadership", Icons.Default.Person, DarkBlue1) { onNavigate("leadership") }
                InfoCard("Achievement", Icons.Default.EmojiEvents, DarkerAchievementBlue.copy(alpha = 0.8f)) { onNavigate("achievement") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoCard("Participation", Icons.Default.Group, DarkerBlue1) { onNavigate("participation") }
                InfoCard("Service", Icons.Default.Autorenew, Color(0xFF034A9E)) { onNavigate("service") }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .width(330.dp)
                    .height(120.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onNavigate("attainment") },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            "Level of Attainment",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
}

@Composable
fun InfoCard(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    val textColor = contentColorFor(backgroundColor)

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
    InfoDetailScreen("Leadership", pairs) { navController.popBackStack() }
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
    InfoDetailScreen("Achievement", list) { navController.popBackStack() }
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
    InfoDetailScreen("Participation", list) { navController.popBackStack() }
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
    InfoDetailScreen("Service", list) { navController.popBackStack() }
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
    // Get current levels from each data class
    val leadershipLevel by leadershipData.currentLevel.collectAsState()
    val serviceLevel by serviceData.level.collectAsState(initial = "0")

    val attendance by participationData.attendance.collectAsState()
    val year = participationData.year

    val participationLevel by remember(attendance, year) {
        derivedStateOf {
            when {
                attendance < 75 || year < 2 -> "0"
                year == 2 -> "1"
                year == 3 -> "2"
                year == 4 -> "3"
                year == 5 -> "4"
                year >= 6 -> "5"
                else -> "0"
            }
        }
    }
    val achievementsLevel by achievementsData.currentHighestLevel.collectAsState(initial = 0)

    var attainmentState by remember { mutableStateOf("Fair") }
    var points by remember { mutableStateOf(0) }
    var attainmentColor by remember { mutableStateOf(Color.Red.copy(alpha = 0.5f)) }

    LaunchedEffect(leadershipLevel, serviceLevel, participationLevel, achievementsLevel) {
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

    // Explanation texts per domain and level
    val leadershipExplanations = listOf(
        "No roles",
        "Completed 2 leadership modules (at least 3h each)",
        "Is atleast a Class Committee or Committee for student-initiated/student-led projects, approved by school, or NYAA Bronze",
        "Is atleast a Class Chairperson, Lower Sec Student Council, Lower Sec Peer Support Leader, Lower Sec ACE Leader, Lower Sec House Leader, Lower Sec DC leader, Lower Sec CCA Exco Committee, Exco for school-wide events, Chairperson/Vice-Chairperson for student-initiated/student-led projects, NYAA Silver/Gold",
        "Is atleast a Upper Sec Student Council, Upper Sec Peer Support Leader, Upper Sec ACE Leader, Upper Sec House Exco, Upper Sec House Captain/Vice-Captain, Upper Sec DC leader, Chairperson/ViceChair person for school-wide events, Upper Sec CCA Exco Committee",
        "Is atleast a Student Council Exco, Peer Support Leader Exco, ACE Leader Exco, House Leader Exco, DC leader Exco, CCA Chairperson/Vice-Chairperson"
    )
    val serviceExplanations = listOf(
        "Less than 24 hours",
        "24 to 30 service hours",
        "30 to 36 service hours and at least 1 VIA project",
        "At least 36 service hours and 2 VIA projects or 24 service hours and 1 VIA project",
        "24 service hours and 2 VIA projects",
        "24 hours of service and 1 student-led project for the community and 1 VIA project"
    )
    val participationExplanations = listOf(
        "Did not participate in CCA enough",
        "Participated in any CCA for 2 years with at least 75% attendance each year",
        "Participated in any CCA for 3 years with at least 75% attendance each year",
        "Participated in any CCA for 4 years with at least 75% attendance each year",
        "Participated in the same CCA for 4 years with exemplary conduct",
        "Participated in the same CCA for 5 years with exemplary conduct"
    )
    val achievementsExplanations = listOf(
        "Not enough achievements",
        "Represented class or CCA at intraschool event",
        "Represented school at a local event for 1 year",
        "Represented school/external at local/international event for 1 year and received top placements",
        "Represented school/external at local/international event for multiple years with top placements or special representation",
        "Represented school at local/international competition and national/international prestigious events"
    )


    @Composable
    fun DomainLevelCard(
        domain: String,
        level: Comparable<*>,
        explanations: List<String>,
        borderColor: Color
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .border(
                    width = 2.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(12.dp)
                ),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "$domain Level: $level",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = borderColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                val levelIndex = when (level) {
                    is Int -> level
                    is String -> level.toIntOrNull() ?: 0
                    else -> 0
                }
                Text(
                    explanations.getOrElse(levelIndex) { "No explanation available." },
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
        }
    }

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
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "$attainmentState: $points Point${if (points == 1) "" else "s"}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = attainmentColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DomainLevelCard(
                domain = "Leadership",
                level = leadershipLevel,
                explanations = leadershipExplanations,
                borderColor = DarkBlue1
            )
            DomainLevelCard(
                domain = "Service",
                level = serviceLevel,
                explanations = serviceExplanations,
                borderColor = Color(0xFF034A9E)
            )
            DomainLevelCard(
                domain = "Participation",
                level = participationLevel,
                explanations = participationExplanations,
                borderColor = DarkerBlue1
            )
            DomainLevelCard(
                domain = "Achievements",
                level = achievementsLevel,
                explanations = achievementsExplanations,
                borderColor = LightBlue1
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoDetailScreen(
    title: String,
    levels: List<Pair<String, String>>,
    onBack: () -> Unit
) {
    Scaffold(topBar = {
        TopAppBarWithBackButton(title, onBack)
    },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
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
                        "Achievement" -> "Achievement levels are for students accomplishments in CCA outside classrooms. These include opportunities to represent the school or other external competition that better caters to your interests and talents."
                        "Participation" -> "Participation is for students participation in CCA. Based on the number of years of participation, their conduct and active contribution."
                        "Service" -> "Service hours are for students who contribute to the community. Students will get hours for planning, service and reflection in either a VIA/SIP/SL project."
                        "Leadership" -> "Leadership levels are for students who develop leadership skills. This includes leadership boards, CCA leadership, or NYAA."
                        else -> ""
                    },
                    fontSize = 16.sp,
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
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
            .clickable { expanded = !expanded }
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(12.dp),
                clip = false
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(12.dp)
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
                FormattedDescription(description)
            }
        }
    }
}


@Composable
fun FormattedDescription(description: String) {
    val lines = description.split('\n')

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        lines.forEach { line ->
            val trimmed = line.trim()
            val annotated = buildAnnotatedString {
                when {
                    trimmed.matches(Regex("""^\d+\.\s+.*""")) -> {
                        val numberEnd = trimmed.indexOf('.') + 1
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(trimmed.substring(0, numberEnd))
                        }
                        append(" ")
                        append(trimmed.substring(numberEnd + 1).trim())
                    }
                    trimmed.startsWith("- ") -> {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                            append("• ")
                        }
                        append(trimmed.substring(2))
                    }
                    else -> append(trimmed)
                }
            }

            Text(
                text = annotated,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }
    }
}

