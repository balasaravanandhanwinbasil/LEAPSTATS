package com.example.leaps20

import EnrichmentView
import com.example.leaps20.UserData
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.ArrowBack
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat


class ServiceDataFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ServiceData::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ServiceData(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

class UserDataFactory(private val context: Context) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserData::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserData(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}


class MainActivity : ComponentActivity() {
    private val userManager = UserManager.shared

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val isUserLoggedIn by userManager.isLoggedIn

            val leadershipData: LeadershipData = viewModel()
            val achievementsData: AchievementsData = viewModel()
            val participationData: ParticipationData = viewModel()
            val serviceData: ServiceData = viewModel(factory = ServiceDataFactory(application))
            val userData: UserData = viewModel(factory = UserDataFactory(application))

            LaunchedEffect(isUserLoggedIn) {
                if (isUserLoggedIn) {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            }

            MaterialTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFB0E0E6).copy(alpha = 0.5f))
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = if (isUserLoggedIn) "home" else "login"
                    ) {
                        composable("login") {
                            LoginScreen(userManager = userManager, navController = navController)
                        }
                        composable("signup") {
                            SignUpScreen(userManager = userManager, navController = navController)
                        }
                        composable("home") {
                            HomeView(
                                navController = navController,
                                serviceData = serviceData,
                                leadershipData = leadershipData,
                                participationData = participationData,
                                achievementsData = achievementsData,
                                userData = userData
                            )
                        }
                        composable("profile") {
                            ProfileView(navController = navController, userData = userData)
                        }
                        composable("leadership") {
                            LeadershipView(navController = navController, dataManager = leadershipData)
                        }
                        composable("achievements") {
                            AchievementsView(navController = navController, achievementsData = achievementsData)
                        }
                        composable("participation") {
                            ParticipationView(navController = navController, participation = participationData)
                        }
                        composable("service") {
                            ServiceHoursView(navController = navController, serviceData = serviceData)
                        }
                        composable("enrichment") {
                            EnrichmentView()
                        }
                        composable("help") {
                            HelpView(navController = navController)
                        }
                        composable("info") {
                            LEAPSApp(
                                outerNavController = navController,
                                userData = userData,
                                leadershipData = leadershipData,
                                participationData = participationData,
                                achievementsData = achievementsData,
                                serviceData = serviceData
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarWithBackButton(title: String, navController: NavHostController) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }
    )
}



@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = "Loading Steps Icon",
                tint = Color(0xFF03709C),
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "LEAPSTATS",
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = Color(0xFF03709C)
            )
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(
                color = Color.Blue,
                strokeWidth = 4.dp,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(
    navController: NavHostController,
    serviceData: ServiceData,
    leadershipData: LeadershipData,
    participationData: ParticipationData,
    achievementsData: AchievementsData,
    userData: UserData
) {
    val userName by userData.name.collectAsState(initial = null)

    // Show loading only if userName is null
    if (userName == null) {
        LoadingScreen()
        return
    }

    // Safe to use non-null userName here
    val safeUserName = userName!!

    // Other data with defaults or nullable states
    val serviceLevel by serviceData.level.collectAsState(initial = 0)
    val totalServiceHours by serviceData.totalHours.collectAsState(initial = 0f)
    val leadershipPosition by leadershipData.currentLeadershipPosition.collectAsState(initial = "N/A")
    val achievementsHighestLevel by achievementsData.currentHighestLevel.collectAsState(initial = 0)
    val participationAttendance by participationData.attendance.collectAsState(initial = 0)
    val userCCA by userData.cca.collectAsState(initial = "N/A")

    // Calculate attainment state & points
    var attainmentState by remember { mutableStateOf("Fair") }
    var points by remember { mutableStateOf(0) }
    var attainmentColor by remember { mutableStateOf(Color.Red.copy(alpha = 0.5f)) }

    LaunchedEffect(
        leadershipPosition,
        achievementsHighestLevel,
        participationAttendance,
        serviceLevel
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
            "Good" -> Color.Yellow.copy(alpha = 0.4f)
            else -> Color.Red.copy(alpha = 0.5f)
        }
    }

    val spacingFactor = 1.23f
    val dx = 90.dp.value * spacingFactor
    val dyShort = 52.dp.value * spacingFactor
    val dyLong = 104.dp.value * spacingFactor

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "LEAPS 2.0",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                },
                actions = {
                    TextButton(onClick = { navController.navigate("help") }) {
                        Icon(Icons.Default.List, contentDescription = "Help")
                        Spacer(Modifier.width(4.dp))
                        Text("Help")
                    }
                }
            )
        }
    ){ paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            HexagonWithContent(
                color = attainmentColor,
                modifier = Modifier
                    .size(140.dp)
                    .shadow(4.dp, HexagonShape())
                    .border(2.dp, Color.Black, HexagonShape())
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$points Point" + if (points == 1) "" else "s",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        attainmentState,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            HexagonButton(
                title = "$safeUserName:\n$userCCA",
                color = Color(0xFF114056),
                textColor = Color.White,
                modifier = Modifier.offset(x = 0.dp, y = (-dyLong).dp)
            ) { navController.navigate("profile") }

            HexagonButton(
                title = "Leadership:\n$leadershipPosition",
                color = Color(0xFF03709C),
                textColor = Color.White,
                modifier = Modifier.offset(x = dx.dp, y = (-dyShort).dp)
            ) { navController.navigate("leadership") }

            HexagonButton(
                title = "Achievements:\nLevel $achievementsHighestLevel",
                color = Color(0xFFCCEFFF),
                textColor = Color.Black,
                modifier = Modifier.offset(x = dx.dp, y = dyShort.dp)
            ) { navController.navigate("achievements") }

            HexagonButton(
                title = "Participation:\n$participationAttendance%",
                color = Color(0xFF03709C),
                textColor = Color.White,
                modifier = Modifier.offset(x = (-dx).dp, y = dyShort.dp)
            ) { navController.navigate("participation") }

            HexagonButton(
                title = "Service:\n$totalServiceHours hours",
                color = Color(0xFFCCEFFF),
                textColor = Color.Black,
                modifier = Modifier.offset(x = (-dx).dp, y = (-dyShort).dp)
            ) { navController.navigate("service") }

            HexagonButton(
                title = "Leaps 2.0",
                icon = Icons.Default.List,
                color = Color.White,
                textColor = Color.Black,
                modifier = Modifier.offset(x = 0.dp, y = dyLong.dp)
            ) { navController.navigate("info") }
        }
    }
}


@Composable
fun HexagonButton(
    title: String,
    icon: ImageVector? = null,
    color: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(130.dp)
            .shadow(8.dp, HexagonShape(), clip = false) // Shadow behind
            .clip(HexagonShape())
            .background(color)
            .border(2.dp, Color.Black, HexagonShape())
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = textColor, modifier = Modifier.size(20.dp))
                Spacer(Modifier.height(6.dp))
            }
            Text(
                text = title,
                color = textColor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 3,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                softWrap = true,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )
        }
    }
}


@Composable
fun HexagonWithContent(
    color: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 12.dp,
                shape = HexagonShape(),
                ambientColor = Color(0x33000000),
                spotColor = Color(0x55000000),
                clip = false
            )
            .clip(HexagonShape())
            .background(color)
            .border(2.dp, Color.Black, HexagonShape()),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}



fun HexagonShape(): Shape = object : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): androidx.compose.ui.graphics.Outline {
        val path = Path().apply {
            val radius = min(size.width, size.height) / 2f
            val centerX = size.width / 2f
            val centerY = size.height / 2f

            moveTo(
                centerX + radius * cos(0.0).toFloat(),
                centerY + radius * sin(0.0).toFloat()
            )
            for (i in 1..5) {
                val angle = 2.0 * PI * i / 6
                lineTo(
                    centerX + radius * cos(angle).toFloat(),
                    centerY + radius * sin(angle).toFloat()
                )
            }
            close()
        }
        return androidx.compose.ui.graphics.Outline.Generic(path)
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp) // fixed height to align content vertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                modifier = Modifier.align(Alignment.Center),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "$title Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        subtitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
