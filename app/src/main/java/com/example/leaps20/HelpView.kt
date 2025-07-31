package com.example.leaps20

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

data class TeamMember(
    val name: String,
    val title: String,
    val icon: @Composable () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpView(navController: NavHostController) {
    val darkBlue1 = Color(0xFF123456)
    val context = LocalContext.current
    val email = "codexleaps2.0@gmail.com"

    val team = listOf(
        TeamMember(
            "Tess Lee Jin En",
            "Chief Executive Officer",
            { Icon(Icons.Default.Person, contentDescription = null, tint = darkBlue1, modifier = Modifier.size(32.dp)) }
        ),
        TeamMember(
            "Vijayaganapathy Pavithraa",
            "Chief Operating Officer",
            { Icon(Icons.Default.Group, contentDescription = null, tint = darkBlue1, modifier = Modifier.size(32.dp)) }
        ),
        TeamMember(
            "Xi Yue Gong",
            "Chief Design Officer",
            { Icon(Icons.Default.Brush, contentDescription = null, tint = darkBlue1, modifier = Modifier.size(32.dp)) }
        ),
        TeamMember(
            "Balasaravanan Dhanwin Basil",
            "Chief Technology Officer (Android)",
            { Icon(Icons.Default.Build, contentDescription = null, tint = darkBlue1, modifier = Modifier.size(32.dp)) }
        ),
        TeamMember(
            "Kesler Ang Kang Zhi",
            "Chief Technology Officer (iOS)",
            { Icon(Icons.Default.Build, contentDescription = null, tint = darkBlue1, modifier = Modifier.size(32.dp)) }
        ),
        TeamMember(
            "Mr Ng Jun Wei",
            "Client",
            { Icon(Icons.Default.Person, contentDescription = null, tint = darkBlue1, modifier = Modifier.size(32.dp)) }
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Acknowledgements", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("About the app/tutorial", fontSize = 18.sp)
            }
            items(team) { member ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(member.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(member.title, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    member.icon()
                }
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
                val annotatedText = buildAnnotatedString {
                    append("For any problems or feedback, contact ")
                    pushStringAnnotation(tag = "EMAIL", annotation = "mailto:$email")
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline)) {
                        append(email)
                    }
                    pop()
                }

                ClickableText(
                    text = annotatedText,
                    style = LocalTextStyle.current.copy(fontSize = 16.sp),
                    modifier = Modifier.padding(vertical = 8.dp),
                    onClick = { offset ->
                        annotatedText.getStringAnnotations(tag = "EMAIL", start = offset, end = offset)
                            .firstOrNull()?.let { annotation ->
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse(annotation.item)
                                }
                                context.startActivity(intent)
                            }
                    }
                )
            }
        }
    }
}
