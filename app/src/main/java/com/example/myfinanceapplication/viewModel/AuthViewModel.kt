package com.example.myfinanceapplication.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val exceptionForRegister = MutableLiveData<String>()
    private val exceptionForLogIn = MutableLiveData<String>()

    fun register(email: String, password: String): LiveData<String> {
        if (email == "" && password == "") {
            exceptionForRegister.value = "Введите логин и пароль"
        } else if (email == "") {
            exceptionForRegister.value = "Введите логин"
        } else if (password == "") {
            exceptionForRegister.value = "Введите пароль"
        } else {
            //var toast = ""
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Регистрация прошла успешно, добавляем пользователя в базу данных
                        //val user = User(email, password)
                        //database.getReference("users").setValue(auth.currentUser!!.uid)
                        //database.getReference("users").child(auth.currentUser!!.uid)
                        exceptionForRegister.value = "Регистрация прошла успешна!"
                    } else {
                        // Ошибка при регистрации
                        exceptionForRegister.value = "Ошибка при регистрации"
                    }
                }
        }
        return exceptionForRegister
    }

    fun logIn(email: String, password: String): LiveData<String> {
        //Log.d("Auth1", auth.currentUser!!.uid)
        //create()
        //database.getReference("users").child(auth.currentUser!!.uid)

        if (email == "" && password == "") {
            exceptionForLogIn.value = "Введите логин и пароль"
        } else if (email == "") {
            exceptionForLogIn.value = "Введите логин"
        } else if (password == "") {
            exceptionForLogIn.value = "Введите пароль"
        } else {
            //CoroutineScope(Dispatchers.Main).launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        // Вход выполнен успешно, переход на MainActivity
                        database.getReference("users").child(auth.currentUser!!.uid)
                        exceptionForLogIn.value = "Добро пожаловать!"
                    } else {
                        // Ошибка при входе
                        exceptionForLogIn.value = "Неверный логин или пароль"
                    }
                }
        }

        return exceptionForLogIn
    }

    fun exit() {
        auth.signOut()
    }
}