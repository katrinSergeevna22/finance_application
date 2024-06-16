package com.example.myfinanceapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.GoalAdapter
import com.example.myapplication.adapter.TipAdapter
import com.example.myfinanceapplication.databinding.ActivityGoalsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class GoalsActivity : AppCompatActivity() {
    lateinit var binding: ActivityGoalsBinding
    private lateinit var viewModel: GoalViewModel
    private lateinit var adapter: GoalAdapter
    val backgroundFragment = BackgroundFragment.newInstance()
    val infoGoalFragment = InfoGoalFragment.newInstance()
    val newAddGoalFragment = AddGoalFragment.newInstance()
    var mainTip = Tip()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(GoalViewModel::class.java)

        lifecycle.coroutineScope.launch {
            viewModel.apply {
                loadGoal()
                loadGoalByCategory()
                loadOneRandomTipLiveData()
            }
        }
        setupUI()
        observeViewModel()

    }

    override fun onStart() {
        super.onStart()
        setupUI()
        observeViewModel()
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun setupUI() {
        binding.toolbarGoal.setNavigationOnClickListener {
            binding.drawerGoal.openDrawer(GravityCompat.START)
        }

        adapter = GoalAdapter {
            viewModel.setSelectedGoal(it)
            viewModel.fragmentIsOpen = true
            supportFragmentManager.beginTransaction()
                .replace(R.id.backgroundFragment, backgroundFragment)
                .addToBackStack(null)
                .commit()

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.place_holder_infoFragment, infoGoalFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.rcView.layoutManager = LinearLayoutManager(this)
        binding.rcView.adapter = adapter
        val itemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        itemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.shape_indent_rcview)!!)
        binding.rcView.addItemDecoration(itemDecoration)

        binding.ibAddGoal.setOnClickListener {
            viewModel.fragmentIsOpen = true

            supportFragmentManager.beginTransaction()
                .replace(R.id.backgroundFragment, backgroundFragment)
                .addToBackStack(null)
                .commit()

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.place_holder_addFragment, newAddGoalFragment)
                .addToBackStack(null)
                .commit()
        }
        binding.svTextAdvice.setOnClickListener { navigateTo("TipsActivity") }
        binding.ibMainTip.setOnClickListener {
            navigateTo("TipsActivityWithSelect")
        }

        binding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_home -> {
                    navigateTo("MainActivity")
                }
                R.id.menu_tips -> {
                    navigateTo("TipsActivity")
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

        binding.toolbarGoal.setOnMenuItemClickListener { item ->
            Log.d("SelectTB", item.toString())
            when (item.itemId) {
                R.id.item_active -> viewModel.setSelectedCategory("Active")
                R.id.item_achieved -> viewModel.setSelectedCategory("Achieved")
                R.id.item_deleted -> viewModel.setSelectedCategory("Deleted")
            }
            true
        }
            /*
        binding.placeHolderInfoFragment.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val rect = Rect()
                newInfoGoalFragment.view?.getGlobalVisibleRect(rect)
                if (!rect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    // Касание произошло за пределами фрагмента
                    supportFragmentManager.beginTransaction().remove(newInfoGoalFragment).commit()

                }
            }
            true
        }

             */
    }
    fun closeFragments(){
        supportFragmentManager.popBackStack()
        supportFragmentManager.popBackStack()
    }
    private fun observeViewModel() {
/*
        viewModel.getGoalsLiveData().observe(this) { goalList ->
            adapter.submitList(goalList)
            binding.tvCount.text = goalList.size.toString()
            adapter.notifyDataSetChanged()
        }

 */

        viewModel.getGoalsCategoryLiveData().observe(this) { goalList ->
            binding.tvCount.text = goalList.size.toString()
            binding.tvTitleGoals.text = viewModel.getTitleOfCategory()
            adapter.submitList(goalList)

            val selectedItem = intent.getStringExtra("selectedItem")
            var selectPosition = 0
            Log.d("1ST", selectedItem.toString())
            for ((index, item) in goalList.withIndex()){
                if (item.titleOfGoal == selectedItem){
                    Log.d("2ST", index.toString())
                    selectPosition = index
                    break
                }
            }
            Log.d("3ST", selectPosition.toString())
            (binding.rcView.layoutManager as LinearLayoutManager).scrollToPosition(selectPosition)

            adapter.notifyDataSetChanged()
        }

        viewModel.getOneRandomTipLiveData().observe(this) { tip ->
            mainTip = tip
            binding.tvTitleTip.text = tip.title
            binding.tvAdvice.text = tip.text
        }
    }
    fun navigateTo(nameActivity: String) {
        when (nameActivity) {
            "MainActivity" -> {
                intent = Intent(this@GoalsActivity, MainActivity::class.java)
                startActivity(intent)
            }

            "TipsActivity" -> {
                intent = Intent(this@GoalsActivity, TipsActivity::class.java)
                startActivity(intent)
            }
            "TipsActivityWithSelect" -> {
                intent = Intent(this@GoalsActivity, TipsActivity::class.java)
                intent.putExtra("selectedItem", mainTip.title)
                startActivity(intent)
            }
            "IncomeActivity" -> {
                intent = Intent(this@GoalsActivity, IncomeActivity::class.java)
                startActivity(intent)
            }

            "ExpenseActivity" -> {
                intent = Intent(this@GoalsActivity, ExpensesActivity::class.java)
                startActivity(intent)
            }
            "AuthActivity"-> {
                AuthViewModel().exit()
                intent = Intent(this@GoalsActivity, AuthActivity::class.java)
                startActivity(intent)
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
