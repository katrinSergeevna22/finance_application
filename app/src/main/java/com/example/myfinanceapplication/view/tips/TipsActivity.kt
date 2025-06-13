package com.example.myfinanceapplication.view.tips

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfinanceapplication.R
import com.example.myfinanceapplication.databinding.ActivityTipsBinding
import com.example.myfinanceapplication.utils.navigationForNavigationView
import com.example.myfinanceapplication.view.tips.adapterTips.TipAdapter
import com.example.myfinanceapplication.viewModel.TipViewModel
import kotlinx.coroutines.launch

class TipsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTipsBinding
    private lateinit var viewModel: TipViewModel
    private lateinit var adapter: TipAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTipsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[TipViewModel::class.java]

        lifecycle.coroutineScope.launch {
            viewModel.loadFinancialAdvices()
        }
        setupUI()
        observeViewModel()

    }

    private fun setupUI() {
        binding.apply {
            toolbar.setNavigationOnClickListener {
                drawerGoal.openDrawer(GravityCompat.START)
            }

            adapter = TipAdapter()
            rcView.layoutManager = LinearLayoutManager(this@TipsActivity)
            rcView.adapter = adapter
            val itemDecoration =
                DividerItemDecoration(this@TipsActivity, LinearLayoutManager.VERTICAL)
            ContextCompat.getDrawable(
                this@TipsActivity,
                R.drawable.shape_indent_rcview
            )?.let {
                itemDecoration.setDrawable(
                    it
                )
            }
            rcView.addItemDecoration(itemDecoration)

            navigationView.setNavigationItemSelectedListener {
                startActivity(
                    navigationForNavigationView(
                        context = this@TipsActivity,
                        itemId = it.itemId
                    )
                )
                true
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeViewModel() {
        viewModel.getTipsLiveData().observe(this) { tipsList ->
            adapter.submitList(tipsList)
            binding.tvNumberTips.text = tipsList.size.toString()

            val selectedItem = intent.getStringExtra("selectedItem")
            var selectPosition = 0
            for ((index, item) in tipsList.withIndex()) {
                if (item.title == selectedItem) {
                    selectPosition = index
                    break
                }
            }
            (binding.rcView.layoutManager as LinearLayoutManager).scrollToPosition(selectPosition)

            adapter.notifyDataSetChanged()
        }
    }
}

