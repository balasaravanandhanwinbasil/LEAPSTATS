package com.example.leaps20

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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Stairs
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.view.WindowCompat
import java.time.LocalDate
import java.util.UUID
import android.graphics.Shader
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.lerp
import com.example.leaps20.ui.theme.LeapStatsTheme
import com.example.leaps20.ui.theme.LeapsBlue


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

            LaunchedEffect(Unit) {
                leadershipData.loadLeadershipPositions()
                achievementsData.loadAchievements()
                participationData.fetchParticipation()
                serviceData.loadServiceEvents()
            }

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

            LeapStatsTheme {
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
                            EnrichmentView(navController = navController)
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
fun FrostedBottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("Home", Icons.Default.Home, "home"),
        BottomNavItem("Enrichment", Icons.Default.School, "enrichment"),
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 40.dp, vertical = 14.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .graphicsLayer {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        renderEffect = android.graphics.RenderEffect
                            .createBlurEffect(
                                20f,
                                20f,
                                android.graphics.Shader.TileMode.CLAMP
                            )
                            .asComposeRenderEffect()
                    }
                }
                .background(Colours.background)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val currentRoute =
                navController.currentBackStackEntryAsState().value?.destination?.route

            items.forEach { item ->
                val selected = currentRoute == item.route

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.clickable {
                        if (!selected) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                ) {
                    Icon(
                        item.icon,
                        contentDescription = item.title,
                        tint = if (selected) Color(0xFF1E88E5) else Colours.text.copy(alpha = 0.75f),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = item.title,
                        fontSize = 12.sp,
                        color = if (selected) Color(0xFF1E88E5) else Colours.text.copy(alpha = 0.75f)
                    )
                }
            }
        }
    }
}


data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)


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
                modifier = Modifier.size(120.dp)
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
    if (userName == null) {
        LoadingScreen()
        return
    }

    val safeUserName = userName!!

    val serviceLevel by serviceData.level.collectAsState(initial = 0)
    val totalServiceHours by serviceData.totalHours.collectAsState(initial = 0f)
    val leadershipPosition by leadershipData.currentLeadershipPosition.collectAsState(initial = "N/A")
    val achievementsHighestLevel by achievementsData.currentHighestLevel.collectAsState(initial = 0)
    val participationAttendance by participationData.attendance.collectAsState(initial = 0)
    val userCCA by userData.cca.collectAsState(initial = "N/A")

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
        containerColor = Colours.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Stairs,
                            contentDescription = null,
                            tint = LeapsBlue
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "LEAPSTATS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp,
                            color = Colours.text
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { navController.navigate("help") },
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .background(
                                lerp(Colours.background, Color.Gray, 0.35f).copy(alpha = 0.6f),
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = "Help",
                                tint = Colours.text.copy(alpha = 0.8f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Help",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Colours.text.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Colours.background.copy(alpha = 0.6f)
                )
            )
        },
        bottomBar = {
            FrostedBottomNavigationBar(navController)
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            // ---- Hexagon content (UNCHANGED) ----
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
                        fontWeight = FontWeight.Bold
                    )
                    Text(attainmentState)
                }
            }

            HexagonButton(
                title = "Profile",
                stat = "$safeUserName\n$userCCA",
                color = Color(0xFF114056),
                textColor = Color.White,
                modifier = Modifier.offset(y = (-dyLong).dp),
                icon = Icons.Default.AccountCircle
            ) { navController.navigate("profile") }

            HexagonButton(
                title = "Leadership",
                stat = leadershipPosition,
                color = Color(0xFF03709C),
                textColor = Color.White,
                modifier = Modifier.offset(x = dx.dp, y = (-dyShort).dp),
                icon = Icons.Default.Person
            ) { navController.navigate("leadership") }

            HexagonButton(
                title = "Achievements",
                stat = "Level $achievementsHighestLevel",
                color = Color(0xFFCCEFFF),
                textColor = Color.Black,
                modifier = Modifier.offset(x = dx.dp, y = dyShort.dp),
                icon = Icons.Default.EmojiEvents
            ) { navController.navigate("achievements") }

            HexagonButton(
                title = "Participation",
                stat = "$participationAttendance%",
                color = Color(0xFF03709C),
                textColor = Color.White,
                modifier = Modifier.offset(x = (-dx).dp, y = dyShort.dp),
                icon = Icons.Default.Group
            ) { navController.navigate("participation") }

            HexagonButton(
                title = "Service",
                stat = "$totalServiceHours hours",
                color = Color(0xFFCCEFFF),
                textColor = Color.Black,
                modifier = Modifier.offset(x = (-dx).dp, y = (-dyShort).dp),
                icon = Icons.Filled.Autorenew
            ) { navController.navigate("service") }

            HexagonButton(
                title = "Leaps 2.0",
                stat = "",
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
    stat: String,
    icon: ImageVector? = null,
    color: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var statFontSize by remember { mutableStateOf(16.sp) }

    Box(
        modifier = modifier
            .size(130.dp)
            .shadow(8.dp, HexagonShape(), clip = false)
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
                style = MaterialTheme.typography.titleSmall.copy(
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = stat,
                color = textColor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { result ->
                    if (result.hasVisualOverflow && statFontSize > 11.sp) {
                        statFontSize *= 0.9f
                    }
                }
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
                .height(48.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Colours.text
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
                        tint = LeapsBlue,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Colours.text
                )
            }
        }

        subtitle?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 4.dp),
                color = Colours.text
            )
        }
    }
}

// Enrichment View

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnrichmentView(navController: NavHostController) {
    val enrichmentData: EnrichmentData = viewModel()
    val events by enrichmentData.events.collectAsState()

    var isEnrichmentSheet by remember { mutableStateOf(false) }
    var editingEvent by remember { mutableStateOf<Event?>(null) }

    var newEventName by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedColor by remember { mutableStateOf(Color(0xFFADD8E6)) }

    fun resetForm() {
        newEventName = ""
        selectedDate = LocalDate.now()
        selectedColor = Color(0xFFADD8E6)
        editingEvent = null
        isEnrichmentSheet = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Enrichment Icon",
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Enrichment", fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        bottomBar = {
            FrostedBottomNavigationBar(
                navController = navController
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Text(
                "Keep track of events and competitions here!",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black
            )

            // Scrollable hexagon list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp), // padding all around
                verticalArrangement = Arrangement.spacedBy((-40).dp) // negative spacing for vertical tightness
            ) {
                itemsIndexed(events) { index, event ->
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (index % 2 == 0)
                                Arrangement.Start else Arrangement.End
                        ) {
                            EnrichmentHexagonView(
                                enrichmentName = event.name,
                                enrichmentDate = event.date,
                                color = event.color,
                                modifier = Modifier
                                    .size(width = 150.dp, height = 140.dp)
                                    .shadow(5.dp, shape = HexagonShape())
                                    .clickable {
                                        editingEvent = event
                                        newEventName = event.name
                                        selectedDate = event.date
                                        selectedColor = event.color
                                        isEnrichmentSheet = true
                                    }
                            )
                        }
                    }
                }

                // Add new event button
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (events.size % 2 == 0) Arrangement.Start else Arrangement.End
                        ) {
                            AddEventHexagon {
                                resetForm()
                                isEnrichmentSheet = true
                            }
                        }
                    }
                }
            }

        }

        // Event form dialog
        if (isEnrichmentSheet) {
            EnrichmentEventForm(
                eventName = newEventName,
                onEventNameChange = { newEventName = it },
                eventDate = selectedDate,
                eventColor = selectedColor,
                onEventColorChange = { selectedColor = it },
                onCancel = { resetForm() },
                onAddOrUpdate = {
                    if (newEventName.isBlank()) return@EnrichmentEventForm

                    val newEvent = Event(
                        id = editingEvent?.id ?: UUID.randomUUID(),
                        name = newEventName,
                        date = selectedDate,
                        color = selectedColor
                    )

                    editingEvent?.let {
                        val index = events.indexOfFirst { e -> e.id == it.id }
                        if (index >= 0) {
                            enrichmentData.updateEnrichmentEvent(index, newEvent) { success ->
                                if (success) resetForm()
                            }
                        }
                    } ?: run {
                        enrichmentData.addEnrichmentEvent(newEvent) { success ->
                            if (success) resetForm()
                        }
                    }
                },
                onDelete = {
                    editingEvent?.let { event ->
                        val index = events.indexOfFirst { e -> e.id == event.id }
                        if (index >= 0) {
                            enrichmentData.removeEnrichmentEvent(index) { success ->
                                if (success) resetForm()
                            }
                        }
                    }
                },
                isEditing = editingEvent != null,
                onDateChange = { selectedDate = it }
            )
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EnrichmentEventForm(
    eventName: String,
    onEventNameChange: (String) -> Unit,
    eventDate: LocalDate,
    eventColor: Color,
    onEventColorChange: (Color) -> Unit,
    onCancel: () -> Unit,
    onAddOrUpdate: () -> Unit,
    onDelete: () -> Unit,
    isEditing: Boolean,
    onDateChange: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateChange(LocalDate.of(year, month + 1, dayOfMonth))
        },
        eventDate.year,
        eventDate.monthValue - 1,
        eventDate.dayOfMonth
    )

    AlertDialog(
        onDismissRequest = { onCancel() }, // closes when background is tapped
        title = { Text(if (isEditing) "Edit Enrichment" else "Add Enrichment") },
        text = {
            Column {
                OutlinedTextField(
                    value = eventName,
                    onValueChange = onEventNameChange,
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Event Date", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(eventDate.toString())
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Color:")
                ColorPicker(eventColor, onEventColorChange)
            }
        },
        confirmButton = {
            Button(onClick = {
                onAddOrUpdate()
                onCancel()
            }) {
                Text(if (isEditing) "Update" else "Add")
            }
        },
        dismissButton = {
            Row {
                if (isEditing) {
                    TextButton(onClick = {
                        onDelete()
                        onCancel()
                    }) {
                        Text("Delete", color = Color.Red)
                    }
                }
                TextButton(onClick = { onCancel() }) {
                    Text("Cancel")
                }
            }
        }
    )

    if (showDatePicker) {
        datePickerDialog.show()
        showDatePicker = false
    }
}

@Composable
fun AddEventHexagon(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 180.dp, height = 190.dp)
            .shadow(5.dp, shape = HexagonShape())
            .background(Color(0xFFF5F5F5), shape = HexagonShape())
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text("+ New", fontSize = MaterialTheme.typography.headlineMedium.fontSize, color = Color.Black)
    }
}

@Composable
fun EnrichmentHexagonView(
    enrichmentName: String,
    enrichmentDate: LocalDate,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(HexagonShape())
            .background(color)
            .border(2.dp, Color.Black, shape = HexagonShape())
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = enrichmentName,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Black,
                maxLines = 2,
                softWrap = true,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = enrichmentDate.toString(),
                fontSize = 12.sp,
                color = Color.Black
            )
        }
    }
}




@Composable
fun ColorPicker(selectedColor: Color, onColorSelected: (Color) -> Unit) {
    val colors = listOf(Color.Red, Color.Green, Color.Blue, Color.Yellow, Color.Cyan, Color.Magenta, Color.Gray)
    Row {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .padding(4.dp)
                    .background(color, shape = CircleShape)
                    .clickable { onColorSelected(color) }
                    .border(
                        width = if (color == selectedColor) 3.dp else 1.dp,
                        color = if (color == selectedColor) Color.Black else Color.LightGray,
                        shape = CircleShape
                    )
            )
        }
    }
}

