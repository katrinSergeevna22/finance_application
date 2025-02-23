package com.example.myfinanceapplication.view

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.myfinanceapplication.MainActivity
import com.example.myfinanceapplication.databinding.ActivityAuthBinding
import com.example.myfinanceapplication.view_model.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding
    lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        setContentView(binding.root)

        setupUI()
    }

    val isForTesting = true
    fun setupUI() {
        binding.apply {
            ibRegister.setOnClickListener {
                val email = binding.etLogin.text.toString()
                val password = binding.etPassword.text.toString()

                if (!password.matches(Regex("^(?=.*[A-Za-z]+|[\\d]+)[A-Za-z\\d!@#\$%^&*()-+=]{8,}\$"))) {
                    Toast.makeText(this@AuthActivity, "Ненадежный пароль", Toast.LENGTH_LONG).show()
                } else {
                    viewModel.register(email, password)
                        .observe(this@AuthActivity, Observer { text ->
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
            ibLogIn.setOnClickListener {
                val email = binding.etLogin.text.toString()
                val password = binding.etPassword.text.toString()
                if (isForTesting) {
                    startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                    finish()
                } else {
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
