package com.example.myfinanceapplication.view.tips

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.TipAdapter
import com.example.myfinanceapplication.MainActivity
import com.example.myfinanceapplication.R
import com.example.myfinanceapplication.view_model.TipViewModel
import com.example.myfinanceapplication.databinding.ActivityTipsBinding
import com.example.myfinanceapplication.view.AuthActivity
import com.example.myfinanceapplication.view.cost.ExpensesActivity
import com.example.myfinanceapplication.view.cost.IncomeActivity
import com.example.myfinanceapplication.view.goals.GoalsActivity
import com.example.myfinanceapplication.view_model.AuthViewModel
import kotlinx.coroutines.launch

class TipsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTipsBinding
    private lateinit var viewModel: TipViewModel
    private lateinit var adapter: TipAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTipsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(
            TipViewModel::class.java)

        lifecycle.coroutineScope.launch{
            viewModel.loadFinancialAdvices()
        }
        setupUI()
        observeViewModel()

    }

    private fun setupUI() {
        binding.apply {
            toolbarGoal.setNavigationOnClickListener {
                drawerGoal.openDrawer(GravityCompat.START)
            }

            adapter = TipAdapter()
            rcView.layoutManager = LinearLayoutManager(this@TipsActivity)
            rcView.adapter = adapter
            val itemDecoration = DividerItemDecoration(this@TipsActivity, LinearLayoutManager.VERTICAL)
            itemDecoration.setDrawable(
                ContextCompat.getDrawable(
                    this@TipsActivity,
                    R.drawable.shape_indent_rcview
                )!!
            )
            rcView.addItemDecoration(itemDecoration)

            navigationView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_goals -> {
                        navigateTo("GoalsActivity")
                    }

                    R.id.menu_home -> {
                        navigateTo("MainActivity")
                    }

                    R.id.menu_income -> {
                        navigateTo("IncomeActivity")
                    }

                    R.id.menu_expense -> {
                        navigateTo("ExpenseActivity")
                    }

                    R.id.menu_exit -> {
                        navigateTo("AuthActivity")
                    }
                }
                true
            }
        }
    }

    private fun observeViewModel() {
        viewModel.getTipsLiveData().observe(this) { tipsList ->
            adapter.submitList(tipsList)
            binding.tvNumberTips.text = tipsList.size.toString()

            val selectedItem = intent.getStringExtra("selectedItem")
            var selectPosition = 0
            Log.d("1ST", selectedItem.toString())
            for ((index, item) in tipsList.withIndex()){
                if (item.title == selectedItem){
                    Log.d("2ST", index.toString())
                    selectPosition = index
                    break
                }
            }
            Log.d("3ST", selectPosition.toString())
            (binding.rcView.layoutManager as LinearLayoutManager).scrollToPosition(selectPosition)

            adapter.notifyDataSetChanged()
        }
    }
    fun navigateTo(nameActivity:String){
        when(nameActivity){
            "GoalsActivity"-> {
                intent = Intent(this@TipsActivity, GoalsActivity::class.java)
                startActivity(intent)
            }
            "MainActivity"-> {
                intent = Intent(this@TipsActivity, MainActivity::class.java)
                startActivity(intent)
            }
            "IncomeActivity"-> {
                intent = Intent(this@TipsActivity, IncomeActivity::class.java)
                startActivity(intent)
            }
            "ExpenseActivity"-> {
                intent = Intent(this@TipsActivity, ExpensesActivity::class.java)
                startActivity(intent)
            }
            "AuthActivity" -> {
                AuthViewModel().exit()
                intent = Intent(this@TipsActivity, AuthActivity::class.java)
                startActivity(intent)
            }
        }

    }
}

