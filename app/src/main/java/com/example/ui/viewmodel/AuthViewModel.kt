package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.supabase.SupabaseClient
import com.example.data.supabase.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Success(val session: UserSession) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(private val supabaseClient: SupabaseClient) : ViewModel() {

    private val _session = MutableStateFlow<UserSession?>(null)
    val session: StateFlow<UserSession?> = _session.asStateFlow()

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = supabaseClient.signIn(email, pass)
            result.fold(
                onSuccess = { s ->
                    _session.value = s
                    _uiState.value = AuthUiState.Success(s)
                },
                onFailure = { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "Đăng nhập thất bại")
                }
            )
        }
    }

    fun register(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = supabaseClient.signUp(email, pass)
            result.fold(
                onSuccess = { s ->
                    _session.value = s
                    _uiState.value = AuthUiState.Success(s)
                },
                onFailure = { e ->
                    _uiState.value = AuthUiState.Error(e.message ?: "Đăng ký thất bại")
                }
            )
        }
    }

    fun logout() {
        _session.value = null
        _uiState.value = AuthUiState.Idle
    }
}
