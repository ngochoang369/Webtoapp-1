package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.DiaryModel
import com.example.data.repository.DiaryRepository
import com.example.data.supabase.UserSession
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DiaryViewModel(
    private val repository: DiaryRepository,
    private val getSession: () -> UserSession?
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedMoodFilter = MutableStateFlow<String?>(null)
    val selectedMoodFilter: StateFlow<String?> = _selectedMoodFilter.asStateFlow()

    private val _selectedTagFilter = MutableStateFlow<String?>(null)
    val selectedTagFilter: StateFlow<String?> = _selectedTagFilter.asStateFlow()

    private val _refreshTrigger = MutableStateFlow(0)

    init {
        viewModelScope.launch {
            val email = getSession()?.email ?: "user@privadiary.app"
            repository.seedSampleEntriesIfEmpty(email)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val userDiaries: StateFlow<List<DiaryModel>> = _refreshTrigger.flatMapLatest {
        val userEmail = getSession()?.email ?: "user@privadiary.app"
        repository.getUserDiaries(userEmail)
    }.combine(combine(_searchQuery, _selectedMoodFilter, _selectedTagFilter) { q, mood, tag ->
        Triple(q, mood, tag)
    }) { entries, (query, moodFilter, tagFilter) ->
        entries.filter { entry ->
            val matchesQuery = query.isBlank() ||
                    entry.title.contains(query, ignoreCase = true) ||
                    entry.content.contains(query, ignoreCase = true) ||
                    entry.tags.any { it.contains(query, ignoreCase = true) }

            val matchesMood = moodFilter == null || entry.mood == moodFilter
            val matchesTag = tagFilter == null || entry.tags.contains(tagFilter)

            matchesQuery && matchesMood && matchesTag
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setMoodFilter(mood: String?) {
        _selectedMoodFilter.value = if (_selectedMoodFilter.value == mood) null else mood
    }

    fun setTagFilter(tag: String?) {
        _selectedTagFilter.value = if (_selectedTagFilter.value == tag) null else tag
    }

    fun saveDiary(
        id: String? = null,
        title: String,
        content: String,
        mood: String,
        tags: List<String>,
        weather: String,
        isE2eeEncrypted: Boolean = true,
        allowAdminAudit: Boolean = true
    ) {
        viewModelScope.launch {
            val session = getSession() ?: UserSession.guestUser()
            repository.saveDiary(
                id = id,
                userId = session.userId,
                userEmail = session.email,
                title = title,
                content = content,
                mood = mood,
                tags = tags,
                weather = weather,
                isE2eeEncrypted = isE2eeEncrypted,
                allowAdminAudit = allowAdminAudit
            )
            _refreshTrigger.value += 1
        }
    }

    fun deleteDiary(id: String) {
        viewModelScope.launch {
            repository.deleteDiary(id)
            _refreshTrigger.value += 1
        }
    }

    fun toggleAdminAudit(id: String, currentAllowed: Boolean) {
        viewModelScope.launch {
            repository.updateAdminAuditPermission(id, !currentAllowed)
            _refreshTrigger.value += 1
        }
    }

    fun refresh() {
        _refreshTrigger.value += 1
    }
}
