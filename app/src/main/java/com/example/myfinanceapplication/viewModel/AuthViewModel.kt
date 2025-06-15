package com.example.myfinanceapplication.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _registerResult = MutableLiveData<Resource<String>>()
    val registerResult: LiveData<Resource<String>> = _registerResult

    private val _loginResult = MutableLiveData<Resource<String>>()
    val loginResult: LiveData<Resource<String>> = _loginResult

    fun register(email: String, password: String) {
        _registerResult.value = Resource.Loading() // Показываем состояние загрузки

        // Валидация ввода
        when {
            email.isBlank() && password.isBlank() -> {
                _registerResult.value = Resource.Error("Введите логин и пароль")
                return
            }

            email.isBlank() -> {
                _registerResult.value = Resource.Error("Введите логин")
                return
            }

            password.isBlank() -> {
                _registerResult.value = Resource.Error("Введите пароль")
                return
            }

            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _registerResult.value = Resource.Error("Введите корректный email")
                return
            }

            password.length < 8 -> {
                _registerResult.value = Resource.Error("Пароль должен содержать минимум 8 символов")
                return
            }

            !password.matches(Regex("^(?=.*[A-Za-z]+|[\\d]+)[A-Za-z\\d!@#\$%^&*()-+=]{8,}\$")) -> {
                _registerResult.value = Resource.Error("Ненадежный пароль")
            }
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _registerResult.value = Resource.Success("Регистрация прошла успешно!")
                } else {
                    // Обработка ошибок Firebase
                    val errorMessage = when (task.exception) {
                        is FirebaseNetworkException -> "Ошибка подключения к интернету"
                        is FirebaseAuthUserCollisionException -> "Пользователь с таким email уже существует"
                        is FirebaseAuthWeakPasswordException -> "Пароль слишком слабый"
                        is FirebaseAuthInvalidCredentialsException -> "Некорректный формат email"
                        else -> "Ошибка при регистрации: ${task.exception?.message ?: "Неизвестная ошибка"}"
                    }
                    _registerResult.value = Resource.Error(errorMessage)
                }
            }
    }

    fun logIn(email: String, password: String) {
        _loginResult.value = Resource.Loading() // Показываем состояние загрузки

        // Валидация ввода
        when {
            email.isBlank() && password.isBlank() -> {
                _loginResult.value = Resource.Error("Введите email и пароль")
                return
            }

            email.isBlank() -> {
                _loginResult.value = Resource.Error("Введите email")
                return
            }

            password.isBlank() -> {
                _loginResult.value = Resource.Error("Введите пароль")
                return
            }

            password.length < 6 -> {
                _loginResult.value = Resource.Error("Пароль должен содержать минимум 6 символов")
                return
            }
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Проверяем, верифицирован ли email (если требуется)
                    val user = auth.currentUser
                    if (user != null) {
                        _loginResult.value = Resource.Success("Добро пожаловать!")
                        // Дополнительные действия после успешного входа
                    }
                } else {
                    // Обработка ошибок Firebase
                    val errorMessage = when (task.exception) {
                        is FirebaseNetworkException -> "Ошибка подключения к интернету"
                        is FirebaseAuthInvalidUserException -> "Пользователь не найден"
                        is FirebaseAuthInvalidCredentialsException -> "Неверный email или пароль"
                        else -> "Ошибка при входе: ${task.exception?.message ?: "Неизвестная ошибка"}"
                    }
                    _loginResult.value = Resource.Error(errorMessage)
                }
            }
    }

    fun exit() {
        auth.signOut()
    }
}

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}