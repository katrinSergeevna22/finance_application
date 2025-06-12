package com.example.myfinanceapplication.view

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myfinanceapplication.MainActivity
import com.example.myfinanceapplication.databinding.ActivityAuthBinding
import com.example.myfinanceapplication.viewModel.AuthViewModel
import com.example.myfinanceapplication.viewModel.Resource

class AuthActivity : AppCompatActivity() {
    lateinit var binding: ActivityAuthBinding
    lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        setContentView(binding.root)

        setupUI()
    }

    private val isForTesting = false
    fun setupUI() {
        binding.apply {
            ibRegister.setOnClickListener {
                val email = binding.etLogin.text.toString()
                val password = binding.etPassword.text.toString()

                if (!password.matches(Regex("^(?=.*[A-Za-z]+|[\\d]+)[A-Za-z\\d!@#\$%^&*()-+=]{8,}\$"))) {
                    Toast.makeText(this@AuthActivity, "Ненадежный пароль", Toast.LENGTH_LONG).show()
                } else {
                    viewModel.register(email, password)
                    viewModel.registerResult.observe(this@AuthActivity) { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                hideLoading()
                                resource.data?.let { text -> showMessage(text) }
                            }

                            is Resource.Error -> {
                                hideLoading()
                                resource.message?.let { text -> showMessage(text) }
                            }

                            is Resource.Loading -> {
                                showLoading()
                            }
                        }
                    }
                }

            }
            ibLogIn.setOnClickListener {
                val email = if (isForTesting) "katrin@mail.ru" else binding.etLogin.text.toString()
                val password = if (isForTesting) "123456789" else binding.etPassword.text.toString()
                viewModel.logIn(email, password)

                viewModel.loginResult.observe(this@AuthActivity) { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            hideLoading()
                            resource.data?.let { text -> showMessage(text) }
                            startActivity(Intent(this@AuthActivity, MainActivity::class.java))
                            finish()
                        }

                        is Resource.Error -> {
                            hideLoading()
                            resource.message?.let { text -> showMessage(text) }
                        }

                        is Resource.Loading -> {
                            showLoading()
                        }
                    }

                }

            }
        }
    }

    private fun showMessage(text: String) {
        Toast.makeText(
            this@AuthActivity,
            text,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoading() {
        binding.apply {
            progressBar?.visibility = View.VISIBLE
            overlay?.visibility = View.VISIBLE
            etLogin.isEnabled = false
            etPassword.isEnabled = false
            ibLogIn.isEnabled = false
            ibRegister.isEnabled = false
        }
    }

    private fun hideLoading() {
        binding.apply {
            progressBar?.visibility = View.GONE
            overlay?.visibility = View.GONE
            etLogin.isEnabled = true
            etPassword.isEnabled = true
            ibLogIn.isEnabled = true
            ibRegister.isEnabled = true
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
