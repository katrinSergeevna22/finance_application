package com.example.myfinanceapplication.view.goals

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfinanceapplication.view.goals.adapterGoals.GoalAdapter
import com.example.myfinanceapplication.R
import com.example.myfinanceapplication.databinding.ActivityGoalsBinding
import com.example.myfinanceapplication.model.Tip
import com.example.myfinanceapplication.utils.NavigationTitle
import com.example.myfinanceapplication.utils.getIntentForNavigation
import com.example.myfinanceapplication.utils.navigationForNavigationView
import com.example.myfinanceapplication.view.BackgroundFragment
import com.example.myfinanceapplication.viewModel.GoalViewModel
import kotlinx.coroutines.launch

class GoalsActivity : AppCompatActivity() {
    lateinit var binding: ActivityGoalsBinding
    private lateinit var viewModel: GoalViewModel
    private lateinit var adapter: GoalAdapter
    private var mainTip = Tip()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[GoalViewModel::class.java]

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
        binding.apply {
            toolbar.setNavigationOnClickListener {
                drawerGoal.openDrawer(GravityCompat.START)
            }

            adapter = GoalAdapter {
                viewModel.setSelectedGoal(it)
                viewModel.fragmentIsOpen = true
                supportFragmentManager.beginTransaction()
                    .replace(R.id.backgroundFragment, BackgroundFragment.newInstance())
                    .addToBackStack(null)
                    .commit()

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.place_holder_infoFragment, InfoGoalFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }

            rcView.layoutManager = LinearLayoutManager(this@GoalsActivity)
            rcView.adapter = adapter
            val itemDecoration =
                DividerItemDecoration(this@GoalsActivity, LinearLayoutManager.VERTICAL)
            itemDecoration.setDrawable(
                ContextCompat.getDrawable(
                    this@GoalsActivity,
                    R.drawable.shape_indent_rcview
                )!!
            )
            rcView.addItemDecoration(itemDecoration)

            ibAddGoal.setOnClickListener {
                viewModel.fragmentIsOpen = true

                supportFragmentManager.beginTransaction()
                    .replace(R.id.backgroundFragment, BackgroundFragment.newInstance())
                    .addToBackStack(null)
                    .commit()

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.place_holder_addFragment, AddGoalFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }
            binding.svTextAdvice.setOnClickListener {
                startActivity(
                    getIntentForNavigation(
                        context = this@GoalsActivity,
                        nameActivity = NavigationTitle.TIPS,
                    )
                )
            }
            binding.ibMainTip.setOnClickListener {
                startActivity(
                    getIntentForNavigation(
                        context = this@GoalsActivity,
                        nameActivity = NavigationTitle.TIPS_WITH_SELECT,
                        titleOfGoalsOrTips = mainTip.title.toString()
                    )
                )
            }

            binding.navigationView.setNavigationItemSelectedListener {
                startActivity(
                    navigationForNavigationView(
                        context = this@GoalsActivity,
                        itemId = it.itemId
                    )
                )
                true
            }

            toolbar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.item_active -> viewModel.setSelectedCategory("Active")
                    R.id.item_achieved -> viewModel.setSelectedCategory("Achieved")
                    R.id.item_deleted -> viewModel.setSelectedCategory("Deleted")
                }
                true
            }
        }
    }

    fun closeFragments() {
        supportFragmentManager.popBackStack()
        supportFragmentManager.popBackStack()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {

        viewModel.getGoalsCategoryLiveData().observe(this) { goalList ->
            binding.tvCount.text = goalList.size.toString()
            binding.tvTitleGoals.text = viewModel.getTitleOfCategory()
            adapter.submitList(goalList)

            val selectedItem = intent.getStringExtra("selectedItem")
            var selectPosition = 0
            for ((index, item) in goalList.withIndex()) {
                if (item.titleOfGoal == selectedItem) {
                    selectPosition = index
                    break
                }
            }
            (binding.rcView.layoutManager as LinearLayoutManager).scrollToPosition(
                selectPosition
            )

            adapter.notifyDataSetChanged()
        }

        viewModel.getOneRandomTipLiveData().observe(this) { tip ->
            mainTip = tip
            binding.tvTitleTip.text = tip.title
            binding.tvAdvice.text = tip.text
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
                    val imm =
                        getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }

        }
        return super.dispatchTouchEvent(event)
    }
}
