package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.DuaCategory
import com.example.data.model.DuaItem
import com.example.data.repository.DuaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DuaRepository
    private var tts: TextToSpeech? = null
    private val vibrator: Vibrator?

    private val _isTtsReady = MutableStateFlow(false)
    val isTtsReady: StateFlow<Boolean> = _isTtsReady.asStateFlow()

    private val _isAudioPlaying = MutableStateFlow(false)
    val isAudioPlaying: StateFlow<Boolean> = _isAudioPlaying.asStateFlow()

    private val _playingDuaId = MutableStateFlow<String?>(null)
    val playingDuaId: StateFlow<String?> = _playingDuaId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("ALL") // "ALL", "QURAN", "HADITH", "BOOKMARKS"
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _arabicFontSize = MutableStateFlow(24f)
    val arabicFontSize: StateFlow<Float> = _arabicFontSize.asStateFlow()

    private val _showTransliteration = MutableStateFlow(true)
    val showTransliteration: StateFlow<Boolean> = _showTransliteration.asStateFlow()

    private val _dailyReminderEnabled = MutableStateFlow(true)
    val dailyReminderEnabled: StateFlow<Boolean> = _dailyReminderEnabled.asStateFlow()

    // Tasbih counter state
    private val _tasbihCount = MutableStateFlow(0)
    val tasbihCount: StateFlow<Int> = _tasbihCount.asStateFlow()

    private val _tasbihTarget = MutableStateFlow(33)
    val tasbihTarget: StateFlow<Int> = _tasbihTarget.asStateFlow()

    val categories: StateFlow<List<DuaCategory>>
    val bookmarkedDuas: StateFlow<List<DuaItem>>
    val recentDuas: StateFlow<List<DuaItem>>
    val quranicDuas: StateFlow<List<DuaItem>>
    val allVerifiedDuas: StateFlow<List<DuaItem>>
    val allDuasForAdmin: StateFlow<List<DuaItem>>
    val dailyDua: StateFlow<DuaItem?>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = DuaRepository(database.duaDao())

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

        viewModelScope.launch {
            repository.initializeDatabaseIfNeeded()
        }

        categories = repository.getAllCategories().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        allVerifiedDuas = repository.getAllVerifiedDuas().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        allDuasForAdmin = repository.getAllDuasForAdmin().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        bookmarkedDuas = repository.getBookmarkedDuas().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        recentDuas = repository.getRecentDuas().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        quranicDuas = repository.getQuranicDuas().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        dailyDua = repository.getRandomDailyDua().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

        initTextToSpeech(application)
    }

    private fun initTextToSpeech(context: Context) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Try Arabic first for Arabic dua recitation, fallback to default
                val arabicLocale = Locale("ar")
                val result = tts?.setLanguage(arabicLocale)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.language = Locale.getDefault()
                }
                _isTtsReady.value = true
            }
        }

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isAudioPlaying.value = true
            }

            override fun onDone(utteranceId: String?) {
                _isAudioPlaying.value = false
                _playingDuaId.value = null
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _isAudioPlaying.value = false
                _playingDuaId.value = null
            }
        })
    }

    fun playAudio(dua: DuaItem) {
        if (_playingDuaId.value == dua.id && _isAudioPlaying.value) {
            stopAudio()
            return
        }

        stopAudio()
        _playingDuaId.value = dua.id

        // Speak the Arabic text
        tts?.let { player ->
            player.language = Locale("ar")
            player.speak(dua.arabicText, TextToSpeech.QUEUE_FLUSH, null, "dua_${dua.id}")
        }
    }

    fun stopAudio() {
        tts?.stop()
        _isAudioPlaying.value = false
        _playingDuaId.value = null
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun clearSearch() {
        _searchQuery.value = ""
    }

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun setArabicFontSize(size: Float) {
        _arabicFontSize.value = size
    }

    fun toggleTransliteration() {
        _showTransliteration.value = !_showTransliteration.value
    }

    fun toggleDailyReminder() {
        _dailyReminderEnabled.value = !_dailyReminderEnabled.value
    }

    fun toggleBookmark(dua: DuaItem) {
        viewModelScope.launch {
            repository.toggleBookmark(dua.id, dua.isBookmarked)
        }
    }

    fun markDuaViewed(duaId: String) {
        viewModelScope.launch {
            repository.recordDuaViewed(duaId)
        }
    }

    fun saveUserNotes(duaId: String, notes: String?) {
        viewModelScope.launch {
            repository.updateUserNotes(duaId, notes)
        }
    }

    // Tasbih Counter Logic
    fun incrementTasbih() {
        val next = _tasbihCount.value + 1
        _tasbihCount.value = next
        triggerHaptic(isTargetReached = (next % _tasbihTarget.value == 0))
    }

    fun resetTasbih(newTarget: Int = _tasbihTarget.value) {
        _tasbihCount.value = 0
        _tasbihTarget.value = newTarget
    }

    fun setTasbihTarget(target: Int) {
        _tasbihTarget.value = target
        _tasbihCount.value = 0
    }

    private fun triggerHaptic(isTargetReached: Boolean) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (isTargetReached) {
                    vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 80, 80, 120), -1))
                } else {
                    vibrator?.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
                }
            } else {
                @Suppress("DEPRECATION")
                if (isTargetReached) {
                    vibrator?.vibrate(longArrayOf(0, 80, 80, 120), -1)
                } else {
                    vibrator?.vibrate(25)
                }
            }
        } catch (_: Exception) {
            // Safe fallback if permission is restricted
        }
    }

    // Content Management / Editorial Operations
    fun saveOrUpdateDua(dua: DuaItem, isNew: Boolean) {
        viewModelScope.launch {
            if (isNew) {
                repository.insertDua(dua)
            } else {
                repository.updateDua(dua)
            }
        }
    }

    fun updateDuaVerificationStatus(dua: DuaItem, newStatus: String, notes: String) {
        viewModelScope.launch {
            val updated = dua.copy(
                verificationStatus = newStatus,
                reviewerNotes = notes
            )
            repository.updateDua(updated)
        }
    }

    fun deleteDua(dua: DuaItem) {
        viewModelScope.launch {
            repository.deleteDua(dua)
        }
    }

    fun resetStarterData() {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(getApplication())
            db.duaDao().insertCategories(com.example.data.local.StarterDuaData.categories)
            db.duaDao().insertDuas(com.example.data.local.StarterDuaData.verifiedDuas)
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
