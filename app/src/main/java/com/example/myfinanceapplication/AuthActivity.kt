package com.example.myfinanceapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import com.example.myfinanceapplication.databinding.ActivityAuthBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import java.lang.Exception

class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding
    lateinit var viewModel: AuthViewModel
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        setContentView(binding.root)

        setupUI()
    }
    fun setupUI(){
        binding.apply {
            ibRegister.setOnClickListener {
                val email = binding.etLogin.text.toString()
                val password = binding.etPassword.text.toString()
                viewModel.register(email, password).observe(this@AuthActivity, Observer{text ->
                    if (text != "") {
                        Toast.makeText(
                            this@AuthActivity,
                            text,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
                //database.reference.child("users").setValue(auth.currentUser!!.uid)
                //database.getReference("users").child(auth.currentUser!!.uid).setValue(auth.currentUser!!.uid)
            }
            ibLogIn.setOnClickListener {
                val email = binding.etLogin.text.toString()
                val password = binding.etPassword.text.toString()
                viewModel.logIn(email, password).observe(this@AuthActivity, Observer { text ->
                    Log.d("Auth2", password.toString())

                    if (text == "Добро пожаловать!") {
                        //database.getReference("users").setValue(auth.currentUser!!.uid)
                        startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                        finish()
                    }
                    if (text != "") {
                        Toast.makeText(
                            this@AuthActivity,
                            text,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })

            }
        }
    }
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}
/*
class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Инициализация Firebase
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()

        // Обработка нажатия на кнопку "Зарегистрировать"
        binding.ibRegister.setOnClickListener {

            val email = binding.etLogin.text.toString()
            val password = binding.etPassword.text.toString()

            if (email == "" || password == "") {
                if (email == "") {
                    Toast.makeText(
                        this@AuthActivity,
                        "Введите логин",
                        Toast.LENGTH_LONG
                    ).show()
                }
                if (password == "") {
                    Toast.makeText(
                        this@AuthActivity,
                        "Введите пароль",
                        Toast.LENGTH_LONG
                    ).show()
                }
                Toast.makeText(
                    this@AuthActivity,
                    "Заполните все поля",
                    Toast.LENGTH_LONG
                ).show()
            }else {

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Регистрация прошла успешно, добавляем пользователя в базу данных
                        val user = User(email, password)
                        database.getReference("users").child(auth.currentUser!!.uid).setValue(user)
                    } else {
                        // Ошибка при регистрации
                        Toast.makeText(this, "Ошибка при регистрации", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Обработка нажатия на кнопку "Войти"
        binding.ibLogIn.setOnClickListener {
            val email = binding.etLogin.text.toString()
            val password = binding.etPassword.text.toString()

                if (email == "" || password == "") {
                    if (email == "") {
                        Toast.makeText(
                            this@AuthActivity,
                            "Введите логин",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    if (password == "") {
                        Toast.makeText(
                            this@AuthActivity,
                            "Введите пароль",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    Toast.makeText(
                        this@AuthActivity,
                        "Заполните все поля",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Вход выполнен успешно, переход на MainActivity
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            } else {
                                // Ошибка при входе
                                Toast.makeText(
                                    this@AuthActivity,
                                    "Неверный логин или пароль",
                                    Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                }

        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}

 */
    /*
    lateinit var binding: ActivityAuthBinding
    val dataRepository = DataRepository()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            var login = ""
            var password = ""
            ibRegister.setOnClickListener {
                login = etLogin.text.toString()
                password = etPassword.text.toString()
                Log.d("BaseEmpty", login + " " + password)

                if (login == "" || password == "") {
                    if (login == "") {
                        etLogin.hint = "Заполните поле"
                    }
                    if (password == "") {
                        etPassword.hint = "Заполните поле"
                    }
                    Toast.makeText(
                        this@AuthActivity,
                        "Заполните все поля",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {

                    // Добавьте нового пользователя в базу данных
                    dataRepository.writeUser(login, password)
                    // Переход к MainActivity
                    navigateToMainActivity()
                }
            }

            ibLogIn.setOnClickListener {
                login = etLogin.text.toString()
                password = etPassword.text.toString()
                Log.d("BaseEmpty", login.toString() + " " + password.toString())

                if (login == "" || password == "") {
                    if (login == "") {
                        etLogin.hint = "Заполните поле"
                    }
                    if (password == "") {
                        etPassword.hint = "Заполните поле"
                    }
                    Toast.makeText(
                        this@AuthActivity,
                        "Заполните все поля",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {

                    if (dataRepository.checkUser(login, password)) {
                        navigateToMainActivity()
                    }

                    else {
                        Toast.makeText(
                            this@AuthActivity,
                            "Неверный логин или пароль",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        //etLogin.currentTextColor = ContextCompat.getColor(this@AuthActivity, R.color.red)
                    }
                    }

                }
            }
        }

    private fun navigateToMainActivity() {
        // Навигация к MainActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}

     */