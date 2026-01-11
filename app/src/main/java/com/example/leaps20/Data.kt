package com.example.leaps20

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import androidx.compose.runtime.State
import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.time.LocalDate.parse
import java.util.UUID


// USER MANAGER

class UserManager private constructor() : ViewModel() {
    companion object {
        val shared: UserManager by lazy { UserManager() }
    }

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    var currentUserID by mutableStateOf<String?>(null)
    var currentUserEmail by mutableStateOf<String?>(null)
    var currentUserName by mutableStateOf<String?>(null)

    // Backing mutable state (private)
    private val _isLoggedIn = mutableStateOf(false)

    // Public read-only state (exposed to Compose)
    val isLoggedIn: State<Boolean> get() = _isLoggedIn

    init {
        val user = auth.currentUser
        if (user != null) {
            currentUserID = user.uid
            currentUserEmail = user.email
            _isLoggedIn.value = true
            fetchUserName(user.uid)
        }
    }

    fun signIn(email: String, password: String, onResult: (Result<Unit>) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    currentUserID = user?.uid
                    currentUserEmail = user?.email
                    _isLoggedIn.value = true
                    user?.uid?.let { fetchUserName(it) }
                    onResult(Result.success(Unit))
                } else {
                    onResult(Result.failure(task.exception ?: Exception("Unknown error")))
                }
            }
    }

    fun logout() {
        try {
            auth.signOut()
            _isLoggedIn.value = false
            currentUserID = null
            currentUserEmail = null
            currentUserName = null
        } catch (e: Exception) {
            println("Logout error: ${e.localizedMessage}")
        }
    }

    fun signUp(name: String, email: String, password: String, onResult: (Result<Unit>) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    currentUserID = user?.uid
                    currentUserEmail = user?.email
                    currentUserName = name

                    user?.uid?.let { uid ->
                        val data = mapOf("name" to name, "email" to email)
                        db.collection("users").document(uid).set(data)
                            .addOnSuccessListener {
                                onResult(Result.success(Unit))
                            }
                            .addOnFailureListener { err ->
                                onResult(Result.failure(err))
                            }
                    }
                } else {
                    onResult(Result.failure(task.exception ?: Exception("Unknown error")))
                }
            }
    }

    private fun fetchUserName(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    currentUserName = document.getString("name")
                }
            }
    }
}


// LEADERSHIP

data class LeadershipPosition(
    val id: String = "",
    val name: String = "",
    val year: Int = 0,
    val level: String = ""
)

class LeadershipData : ViewModel() {
    private val _leadershipHexes = MutableStateFlow<List<LeadershipPosition>>(emptyList())
    val leadershipHexes: StateFlow<List<LeadershipPosition>> = _leadershipHexes.asStateFlow()

    private val _currentLeadershipPosition = MutableStateFlow("No role")
    val currentLeadershipPosition: StateFlow<String> = _currentLeadershipPosition.asStateFlow()

    private val _currentLevel = MutableStateFlow("0")
    val currentLevel: StateFlow<String> = _currentLevel.asStateFlow()

    private val db = Firebase.firestore
    private val userID: String? get() = FirebaseAuth.getInstance().currentUser?.uid

    init {
        loadLeadershipPositions()
    }

    fun loadLeadershipPositions() {
        val uid = userID ?: return
        db.collection("users").document(uid).collection("leadership").get()
            .addOnSuccessListener { snapshot ->
                _leadershipHexes.value = snapshot.documents.mapNotNull { doc ->
                    val data = doc.data ?: return@mapNotNull null
                    val name = data["name"] as? String ?: return@mapNotNull null
                    val year = (data["year"] as? Long)?.toInt() ?: return@mapNotNull null
                    val level = data["level"] as? String ?: return@mapNotNull null
                    LeadershipPosition(id = doc.id, name = name, year = year, level = level)
                }
                updateCurrentHighestLevel()
            }
            .addOnFailureListener { e ->
                Log.e("LeadershipData", "Error fetching leadership data: ${e.localizedMessage}")
            }
    }

    fun addLeadershipPosition(name: String, year: Int) {
        val uid = userID ?: return
        val level = getLevelForPosition(name)
        val docRef = db.collection("users").document(uid).collection("leadership").document()
        val position = LeadershipPosition(id = docRef.id, name = name, year = year, level = level)

        docRef.set(mapOf("name" to name, "year" to year, "level" to level))
            .addOnSuccessListener {
                _leadershipHexes.value = _leadershipHexes.value + position
                updateCurrentHighestLevel()
            }
            .addOnFailureListener {
                Log.e("LeadershipData", "Failed to add position: ${it.localizedMessage}")
            }
    }

    fun updateLeadershipPosition(index: Int, name: String, year: Int) {
        val uid = userID ?: return
        if (index >= _leadershipHexes.value.size) return
        val id = _leadershipHexes.value[index].id
        val level = getLevelForPosition(name)

        val data = mapOf("name" to name, "year" to year, "level" to level)
        db.collection("users").document(uid).collection("leadership").document(id).set(data)
            .addOnSuccessListener {
                _leadershipHexes.value = _leadershipHexes.value.toMutableList().apply {
                    set(index, LeadershipPosition(id = id, name = name, year = year, level = level))
                }
                updateCurrentHighestLevel()
            }
            .addOnFailureListener {
                Log.e("LeadershipData", "Update failed: ${it.localizedMessage}")
            }
    }

    fun removeLeadershipPosition(index: Int) {
        val uid = userID ?: return
        if (index >= _leadershipHexes.value.size) return
        val id = _leadershipHexes.value[index].id

        db.collection("users").document(uid).collection("leadership").document(id).delete()
            .addOnSuccessListener {
                _leadershipHexes.value = _leadershipHexes.value.toMutableList().apply { removeAt(index) }
                updateCurrentHighestLevel()
            }
            .addOnFailureListener {
                Log.e("LeadershipData", "Delete failed: ${it.localizedMessage}")
            }
    }

    private fun updateCurrentHighestLevel() {
        if (_leadershipHexes.value.isEmpty()) {
            _currentLeadershipPosition.value = "No role"
            _currentLevel.value = "0"
            return
        }

        val highest = _leadershipHexes.value.maxByOrNull { it.level.toIntOrNull() ?: 0 }
        if (highest != null) {
            _currentLeadershipPosition.value = highest.name
            _currentLevel.value = highest.level
        }
    }

    fun getLevelForPosition(position: String): String {
        return when {
            levelOneArray.contains(position) -> "1"
            levelTwoArray.contains(position) -> "2"
            levelThreeArray.contains(position) -> "3"
            levelFourArray.contains(position) -> "4"
            levelFiveArray.contains(position) -> "5"
            else -> "0"
        }
    }

    companion object {
        val allCategories = listOf("ACE Board", "PSB Board", "SC Board", "DC Board", "House/SNW", "Projects Board", "Others")

        val levelOneArray = listOf("Completed 2 modules on leadership")

        val levelTwoArray = listOf(
            "Class Exco", "Junior SNW Leader", "Committee for SIP", "Committee for SL",
            "Junior CCA Exco", "NYAA Bronze"
        )

        val levelThreeArray = listOf(
            "Class Chairperson", "Class Vice-Chairperson", "Junior SC", "Junior PSB", "Junior ACE", "Junior DC",
            "Junior house captain", "Junior house vice-captain", "Senior Sports Leader",
            "Committee for school-wide events", "Chairperson for SIP", "Vice-Chairperson for SIP",
            "Chairperson for SL projects", "Vice-Chairperson for SL projects",
            "Junior CCA Chairperson", "Junior CCA Vice-Chairperson", "Junior CCA Exco", "NYAA Silver"
        )

        val levelFourArray = listOf(
            "Senior SC", "Senior PSB", "Senior ACE", "Senior DC", "House Exco", "Senior House Captain",
            "Senior House Vice-Captain", "Senior CCA Exco", "Chairperson for school-wide events", "Vice-Chairperson for school-wide events"
        )

        val levelFiveArray = listOf(
            "SC Exco", "PSB Exco", "ACE Exco", "DC Exco",
            "SC President", "SC Vice-President",
            "PSB President", "PSB Vice-President",
            "ACE President", "ACE Vice-President",
            "DC President", "DC Vice-President",
            "CCA Chairperson", "CCA Vice-Chairperson"
        )

        val dcArray = listOf("Junior DC", "Senior DC", "DC Exco", "DC President", "DC Vice-President")
        val aceArray = listOf("Junior ACE", "Senior ACE", "ACE Exco", "ACE President", "ACE Vice-President")
        val scArray = listOf("Junior SC", "Senior SC", "SC Exco", "SC President", "SC Vice-President")
        val psbArray = listOf("Junior PSB", "Senior PSB", "PSB Exco", "PSB President", "PSB Vice-President")
        val ccaArray = listOf(
            "Junior CCA Chairperson", "Junior CCA Vice-Chairperson", "Junior CCA Exco",
            "Senior CCA Exco", "CCA Chairperson", "CCA Vice-Chairperson"
        )
        val houseArray = listOf(
            "Junior house captain", "Junior house vice-captain", "House Exco",
            "Senior House Captain", "Senior House Vice-Captain", "Junior SNW Leader", "Senior Sports Leader"
        )
        val projectArray = listOf(
            "Committee for SIP", "Chairperson for SIP", "Vice-Chairperson for SIP",
            "Committee for school-wide events", "Chairperson for school-wide events", "Vice-Chairperson for school-wide events",
            "Chairperson for SL projects", "Vice-Chairperson for SL projects", "Committee for SL"
        )
        val othersArray = listOf(
            "NYAA Bronze", "NYAA Silver", "Class Chairperson", "Class Vice-Chairperson", "Class Exco", "Completed 2 modules on leadership"
        )

        fun getPositionsForCategory(category: String): List<String> {
            return when (category) {
                "ACE Board" -> aceArray
                "PSB Board" -> psbArray
                "SC Board" -> scArray
                "DC Board" -> dcArray
                "CCA Leaders" -> ccaArray
                "House/SNW" -> houseArray
                "Projects Board" -> projectArray
                "Others" -> othersArray
                else -> emptyList()
            }
        }
    }
}

// ACHIEVEMENTS
data class Achievement(
    val id: String? = null,
    val name: String = "",
    val award: String = "",
    val representation: String = "",
    val level: String = "",
    val year: String = ""
)



class AchievementsData : ViewModel() {

    var hexes = mutableStateListOf<Achievement>()
        private set

    var name = mutableStateOf("")

    private val _currentHighestLevel = MutableStateFlow("0")
    val currentHighestLevel: StateFlow<String> = _currentHighestLevel.asStateFlow()

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var listener: ListenerRegistration? = null

    init {
        loadAchievements()
    }

    private fun uid(): String? = auth.currentUser?.uid

    fun loadAchievements() {
        val userId = uid() ?: return
        listener?.remove()
        listener = db.collection("users").document(userId)
            .collection("achievements")
            .orderBy("level", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val docs = snapshot?.documents ?: return@addSnapshotListener
                hexes.clear()
                for (doc in docs) {
                    val achievement = doc.toObject<Achievement>()?.copy(id = doc.id)
                    if (achievement != null) hexes.add(achievement)
                }
                updateCurrentLevel()
            }
    }

    private fun computeLevel(representation: String): String {
        return when (representation) {
            "National (SG/MOE/UG HQ)" -> "5"
            "School/External Organisation" -> "3"
            "Intra-school" -> "1"
            else -> "0"
        }
    }

    fun addAchievement(
        name: String,
        award: String,
        representation: String,
        year: String,
        onComplete: (Boolean) -> Unit
    ) {
        val userId = uid() ?: return onComplete(false)
        val level = computeLevel(representation)
        val newAchievement = Achievement(
            name = name,
            award = award,
            representation = representation,
            level = level,
            year = year
        )
        db.collection("users").document(userId)
            .collection("achievements")
            .add(newAchievement)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun updateAchievement(
        index: Int,
        name: String,
        award: String,
        representation: String,
        year: String,
        onComplete: (Boolean) -> Unit
    ) {
        val userId = uid() ?: return onComplete(false)
        if (index >= hexes.size) return onComplete(false)

        val existing = hexes[index]
        val docId = existing.id ?: return onComplete(false)

        val updated = Achievement(
            id = docId,
            name = name,
            award = award,
            representation = representation,
            level = computeLevel(representation),
            year = year
        )

        db.collection("users").document(userId)
            .collection("achievements")
            .document(docId)
            .set(updated)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }

    fun removeAchievement(index: Int) {
        val userId = uid() ?: return
        if (index >= hexes.size) return

        val docId = hexes[index].id ?: return

        db.collection("users").document(userId)
            .collection("achievements")
            .document(docId)
            .delete()
    }

    private fun updateCurrentLevel() {
        var nationalCount = 0
        var externalCount = 0
        var intraCount = 0
        val nationalYears = mutableSetOf<String>()
        val externalYears = mutableSetOf<String>()

        for (achievement in hexes) {
            when (achievement.representation) {
                "National (SG/MOE/UG HQ)" -> {
                    nationalCount++
                    nationalYears.add(achievement.year)
                }
                "School/External Organisation" -> {
                    externalCount++
                    externalYears.add(achievement.year)
                }
                "Intra-school" -> {
                    intraCount++
                }
            }
        }

        _currentHighestLevel.value = when {
            nationalCount >= 2 && nationalYears.size >= 2 -> "5"
            nationalCount >= 1 && externalCount >= 2 -> "4"
            externalCount >= 2 -> "3"
            externalCount >= 1 -> "2"
            intraCount >= 1 -> "1"
            else -> "0"
        }
    }

    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }
}

// PARTICIPATION

class ParticipationData : ViewModel() {

    private val _attendance = MutableStateFlow(80)
    val attendance: StateFlow<Int> get() = _attendance

    var year by mutableStateOf(3)
    var level by mutableStateOf("3")

    private val db = FirebaseFirestore.getInstance()

    init { fetchParticipation() }

    fun fetchParticipation() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    _attendance.value = document.getLong("participationAttendance")?.toInt() ?: 0
                    year = document.getLong("participationYear")?.toInt() ?: 1
                }
            }
            .addOnFailureListener { error ->
                println("Error fetching participation data: ${error.localizedMessage}")
            }
    }

    fun updateParticipation(newAttendance: Int, newYear: Int) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        if (newAttendance !in 0..100 || newYear !in 1..10) return // adjust range as needed

        val updates = mapOf(
            "participationAttendance" to newAttendance,
            "participationYear" to newYear
        )

        db.collection("users").document(uid)
            .update(updates)
            .addOnSuccessListener {
                _attendance.value = newAttendance
                year = newYear
            }
            .addOnFailureListener { error ->
                println("Error updating participation data: ${error.localizedMessage}")
            }
    }
}

data class ServiceEvent(
    val id: String? = null,
    val name: String = "",
    val hours: Int = 0,
    val type: String = ""
)

class ServiceData(application: Application) : AndroidViewModel(application) {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _hexes = MutableStateFlow<List<ServiceEvent>>(emptyList())
    val hexes: StateFlow<List<ServiceEvent>> = _hexes.asStateFlow()

    private val _totalHours = MutableStateFlow(0)
    val totalHours: StateFlow<Int> = _totalHours.asStateFlow()

    private val _level = MutableStateFlow("0")
    val level: StateFlow<String> = _level.asStateFlow()

    private val _serviceVIA = MutableStateFlow(0)
    val serviceVIA: StateFlow<Int> = _serviceVIA.asStateFlow()

    private val _serviceSIP = MutableStateFlow(0)
    val serviceSIP: StateFlow<Int> = _serviceSIP.asStateFlow()

    private val uid: String?
        get() = auth.currentUser?.uid

    init {
        loadServiceEvents()
    }

    fun loadServiceEvents() {
        uid?.let { userId ->
            db.collection("users").document(userId)
                .collection("serviceEvents")
                .addSnapshotListener { snapshot, _ ->
                    snapshot?.let {
                        val events = it.documents.mapNotNull { doc ->
                            doc.toObject(ServiceEvent::class.java)?.copy(id = doc.id)
                        }
                        _hexes.value = events
                        calculateTotals()
                    }
                }
        }
    }

    fun addServiceEvent(name: String, hours: Int, type: String, onComplete: (Boolean) -> Unit) {
        uid?.let { userId ->
            val newEvent = ServiceEvent(name = name, hours = hours, type = type)
            db.collection("users").document(userId)
                .collection("serviceEvents")
                .add(newEvent)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } ?: onComplete(false)
    }

    fun updateServiceEvent(index: Int, name: String, hours: Int, type: String, onComplete: (Boolean) -> Unit) {
        val eventList = _hexes.value
        val id = eventList.getOrNull(index)?.id
        if (uid != null && id != null) {
            val updated = ServiceEvent(id = id, name = name, hours = hours, type = type)
            db.collection("users").document(uid!!)
                .collection("serviceEvents").document(id)
                .set(updated)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } else {
            onComplete(false)
        }
    }

    fun removeServiceEvent(index: Int, onComplete: (Boolean) -> Unit = {}) {
        val eventList = _hexes.value
        val id = eventList.getOrNull(index)?.id
        if (uid != null && id != null) {
            db.collection("users").document(uid!!)
                .collection("serviceEvents").document(id)
                .delete()
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } else {
            onComplete(false)
        }
    }

    private fun calculateTotals() {
        val events = _hexes.value
        _totalHours.value = events.sumOf { it.hours }
        _serviceVIA.value = events.count { it.type == "VIA for school/community" }
        _serviceSIP.value = events.count { it.type == "SIP for school/community" }
        updateLevel()
    }

    fun updateLevel() {
        val total = _totalHours.value
        val via = _serviceVIA.value
        val sip = _serviceSIP.value

        _level.value = when {
            total < 24 && via == 0 && sip < 1 -> "0"
            total in 24..30 && via == 0 && sip < 1 -> "1"
            total in 31..36 && via == 0 && sip < 1 -> "2"
            via == 1 && total <= 24 && sip < 1 -> "2"
            total > 36 && via == 0 && sip < 1 -> "3"
            via >= 2 && total < 24 && sip < 1 -> "3"
            total >= 24 && via == 1 && sip < 1 -> "3"
            total >= 24 && via >= 2 && sip < 1 -> "4"
            total >= 24 && sip >= 1 && via >= 1 -> "1"
            else -> "0"
        }
    }
}





data class LevelPoints(
    val level_one: Int = 0,
    val level_two: Int = 0,
    val level_three: Int = 0,
    val level_four: Int = 0,
    val level_five: Int = 0
)

class LevelPointsData {
    var levelPoints = LevelPoints()
        private set

    fun calculatePoints(
        leadership: LeadershipData,
        service: ServiceData,
        participation: ParticipationData,
        achievements: AchievementsData
    ) {
        levelPoints = LevelPoints()
        listOf(
            leadership.currentLevel.value.toIntOrNull(),
            service.level.value.toIntOrNull(),
            achievements.currentHighestLevel.value.toIntOrNull(),
            participation.level.toIntOrNull()
        ).forEach { level ->
            level?.let { addPointToLevel(it) }
        }
    }

    private fun addPointToLevel(level: Any) {
        levelPoints = when (level) {
            1 -> levelPoints.copy(level_one = levelPoints.level_one + 1)
            2 -> levelPoints.copy(level_two = levelPoints.level_two + 1)
            3 -> levelPoints.copy(level_three = levelPoints.level_three + 1)
            4 -> levelPoints.copy(level_four = levelPoints.level_four + 1)
            5 -> levelPoints.copy(level_five = levelPoints.level_five + 1)
            else -> levelPoints
        }
    }
}

class UserData(context: Context) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val uid: String? = FirebaseAuth.getInstance().currentUser?.uid
    private val prefs = context.getSharedPreferences("UserDataPrefs", Context.MODE_PRIVATE)

    val name = MutableStateFlow(prefs.getString("userName", "My Name") ?: "My Name")
    val year = MutableStateFlow(prefs.getInt("userYear", 2025))
    val house = MutableStateFlow(prefs.getString("userHouse", "My House") ?: "My House")
    val cca = MutableStateFlow(prefs.getString("userCCA", "My CCA") ?: "My CCA")
    val points = MutableStateFlow(prefs.getInt("userPoints", 0))
    val state = MutableStateFlow(prefs.getString("userState", "Fair") ?: "Fair")
    @OptIn(ExperimentalEncodingApi::class)
    val profileImageData = MutableStateFlow(value = prefs.getString("userProfileImage", null)?.let { Base64.decode(it
    ) })
    val levelPointsData = MutableStateFlow(LevelPointsData())

    init {
        listenToFirestore()
    }

    private fun getUserDocument(): DocumentReference? {
        return uid?.let { db.collection("users").document(it) }
    }

    fun listenToFirestore() {
        getUserDocument()?.addSnapshotListener { snapshot, _ ->
            snapshot?.data?.let { data ->
                name.value = data["name"] as? String ?: name.value
                year.value = (data["year"] as? Long)?.toInt() ?: year.value
                house.value = data["house"] as? String ?: house.value
                cca.value = data["cca"] as? String ?: cca.value
                saveLocal()
            }
        }
    }

    fun saveField(key: String, value: Any) {
        getUserDocument()?.set(mapOf(key to value), com.google.firebase.firestore.SetOptions.merge())
        prefs.edit().putString("user${key.capitalize()}", value.toString()).apply()
    }

    fun saveLocal() {
        prefs.edit().apply {
            putString("userName", name.value)
            putInt("userYear", year.value)
            putString("userHouse", house.value)
            putString("userCCA", cca.value)
            apply()
        }
    }

    fun resetToDefaults() {
        name.value = "My Name"
        year.value = 2025
        house.value = "My House"
        cca.value = "My CCA"
        saveLocal()
    }

    fun clearStoredData() {
        prefs.edit().clear().apply()
    }

    fun attainment(leadership: LeadershipData, service: ServiceData, participation: ParticipationData, achievements: AchievementsData): String {
        val leadershipLevel = leadership.currentLevel.value.toIntOrNull() ?: 0
        val achievementsLevel = achievements.currentHighestLevel.value.toIntOrNull() ?: 0
        val participationLevel = participation.level.toIntOrNull() ?: 0
        val serviceLevel = service.level.value.toIntOrNull() ?: 0

        val domainLevels = listOf(leadershipLevel, achievementsLevel, participationLevel, serviceLevel)

        val level1OrAbove = domainLevels.count { it >= 1 }
        val level2OrAbove = domainLevels.count { it >= 2 }
        val level3OrAbove = domainLevels.count { it >= 3 }
        val level4OrAbove = domainLevels.count { it >= 4 }

        return if (level3OrAbove >= 4 && level4OrAbove >= 1) {
            "Excellent"
        } else if (level1OrAbove == 4) {
            when {
                level2OrAbove >= 3 -> "Good"
                level2OrAbove >= 1 && level3OrAbove >= 1 -> "Good"
                level4OrAbove >= 1 -> "Good"
                else -> "Fair"
            }
        } else {
            "Fair"
        }
    }
}

data class Event(
    var name: String,
    var date: LocalDate,
    var color: Color = Color(0xFFADD8E6),
    val id: UUID = UUID.randomUUID(),
    val docId: String = ""
)

data class EventDTO(
    val name: String = "",
    val date: String = "",
    val color: Long = 0xFFADD8E6L,
    val id: String = ""
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toEvent(): Event {
        return Event(
            name = name,
            date = LocalDate.parse(date),
            color = Color(color.toULong()),
            id = UUID.fromString(id)
        )
    }

    companion object {
        fun fromEvent(event: Event): EventDTO {
            return EventDTO(
                name = event.name,
                date = event.date.toString(),
                color = event.color.value.toLong(),
                id = event.id.toString()
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
class EnrichmentData(application: Application) : AndroidViewModel(application) {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _totalEvents = MutableStateFlow(0)
    val totalEvents: StateFlow<Int> = _totalEvents.asStateFlow()

    private val uid: String?
        get() = auth.currentUser?.uid

    init {
        loadEnrichmentEvents()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadEnrichmentEvents() {
        uid?.let { userId ->
            db.collection("users").document(userId)
                .collection("enrichments")
                .addSnapshotListener { snapshot, _ ->
                    snapshot?.let {
                        val loadedEvents = it.documents.mapNotNull { doc ->
                            doc.toObject(EventDTO::class.java)
                                ?.toEvent()
                                ?.copy(docId = doc.id) // attach Firestore doc ID
                        }
                        _events.value = loadedEvents
                        _totalEvents.value = loadedEvents.size
                    }
                }
        }
    }

    fun addEnrichmentEvent(event: Event, onComplete: (Boolean) -> Unit) {
        uid?.let { userId ->
            val eventDTO = EventDTO.fromEvent(event)
            db.collection("users").document(userId)
                .collection("enrichments")
                .add(eventDTO)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } ?: onComplete(false)
    }

    fun updateEnrichmentEvent(index: Int, updatedEvent: Event, onComplete: (Boolean) -> Unit) {
        val eventList = _events.value
        val docId = eventList.getOrNull(index)?.docId

        if (uid != null && docId != null) {
            val eventDTO = EventDTO.fromEvent(updatedEvent.copy(id = UUID.fromString(eventList[index].id.toString())))
            db.collection("users").document(uid!!)
                .collection("enrichments")
                .document(docId)
                .set(eventDTO)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } else {
            onComplete(false)
        }
    }

    fun removeEnrichmentEvent(index: Int, onComplete: (Boolean) -> Unit = {}) {
        val eventList = _events.value
        val docId = eventList.getOrNull(index)?.docId
        if (uid != null && docId != null) {
            db.collection("users").document(uid!!)
                .collection("enrichments")
                .document(docId)
                .delete()
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        } else {
            onComplete(false)
        }
    }
}

object Colours {
    val text: Color
        @Composable get() = MaterialTheme.colorScheme.onBackground

    val background: Color
        @Composable get() = MaterialTheme.colorScheme.background
}




