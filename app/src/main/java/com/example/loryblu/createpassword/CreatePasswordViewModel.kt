package com.example.loryblu.createpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loryblu.R
import com.example.loryblu.util.PasswordInputValid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiStateCreatePassword(
    val showPassword: Boolean = false,
    val showConfirmationPassword: Boolean = false,
    val password: String = "",
    val confirmationPassword: String = "",
    val passwordState: PasswordInputValid = PasswordInputValid.Empty,
    val passwordErrors: Map<Int, Boolean> = mapOf(
        R.string.MoreThanEight to false,
        R.string.Uppercase to false,
        R.string.Lowercase to false,
        R.string.Numbers to false,
        R.string.SpecialCharacters to false
    ),
    val equalsPassword: Boolean? = null
)

class CreatePasswordViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiStateCreatePassword())
    val uiState = _uiState

    fun updatePassword(newPassword: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(password = newPassword)
            }
        }
    }

    fun updateConfirmationPassword(newConfirmationPassword: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(confirmationPassword = newConfirmationPassword)
            }
        }
    }

    fun togglePassword() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(showPassword = it.showPassword.not())
            }
        }
    }
    fun toggleConfirmationPassword() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(showConfirmationPassword = it.showConfirmationPassword.not())
            }
        }
    }

    fun passwordCheck() {
        val password = uiState.value.password
        val passwordErrors = _uiState.value.passwordErrors.toMutableMap()

        passwordErrors[R.string.MoreThanEight] = password.length > 8
        passwordErrors[R.string.Uppercase] = password.any { it.isUpperCase() }
        passwordErrors[R.string.Lowercase] = password.any { it.isLowerCase() }
        passwordErrors[R.string.Numbers] = password.any { it.isDigit() }
        passwordErrors[R.string.SpecialCharacters] = password.any { !it.isLetterOrDigit() }

        val passwordState = if (passwordErrors.values.contains(false)) {
            PasswordInputValid.Error(R.string.password_invalid)
        } else {
            PasswordInputValid.Valid
        }

        _uiState.update {
            it.copy(passwordErrors = passwordErrors, passwordState = passwordState)
        }
    }

    fun verifyConfirmationPassword() {
        val password = uiState.value.confirmationPassword

        if(password.isNotEmpty()) {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(equalsPassword = (password == _uiState.value.password))
                }
            }
        }
    }
}