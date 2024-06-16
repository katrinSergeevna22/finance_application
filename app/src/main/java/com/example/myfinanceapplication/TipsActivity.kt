package com.example.myfinanceapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.TipAdapter
import com.example.myfinanceapplication.databinding.ActivityTipsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class TipsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTipsBinding
    private lateinit var viewModel: TipViewModel
    private lateinit var adapter: TipAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTipsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(TipViewModel::class.java)

        lifecycle.coroutineScope.launch{
            viewModel.loadFinancialAdvices()
        }
        setupUI()
        observeViewModel()

        binding.navigationView.setNavigationItemSelectedListener {
            when(it.itemId){
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

    private fun setupUI() {
        binding.toolbarGoal.setNavigationOnClickListener {
            binding.drawerGoal.openDrawer(GravityCompat.START)
        }

        adapter = TipAdapter()
        binding.rcView.layoutManager = LinearLayoutManager(this)
        binding.rcView.adapter = adapter
        val itemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        itemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.shape_indent_rcview)!!)
        binding.rcView.addItemDecoration(itemDecoration)
    }

    private fun observeViewModel() {
        viewModel.getTipsLiveData().observe(this) { tipsList ->
            adapter.submitList(tipsList)
            binding.tvNumberTips.text = tipsList.size.toString()
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
            "AuthActivity"-> {
                intent = Intent(this@TipsActivity, AuthActivity::class.java)
                startActivity(intent)
            }
        }

    }
}




/*
    lateinit var binding:ActivityTipsBinding
    private val dataRepository = DataRepository()

    private var tipsList: MutableList<Tip> = mutableListOf()
    private var countOfTips = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTipsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarGoal.setNavigationOnClickListener {
            binding.drawerGoal.openDrawer(GravityCompat.START)
        }
    }

    override fun onStart() {
        super.onStart()

        var adapter = TipAdapter()

        adapter.submitList(tipsList)

        lifecycle.coroutineScope.launch {
            // Слушатель для получения списка целей из базы данных
            /*
            val tipsRef = database.getReference("users").child(userId).child("tips")
            tipsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    tipsList.clear()

                    for (tipSnapshot in snapshot.children) {
                        val goal = tipSnapshot.getValue(Tip::class.java)
                        goal?.let {
                            tipsList.add(it)
                        }
                    }

                    countOfTips = tipsList.size
                    binding.tvNumberTips.text = countOfTips.toString()
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("TipActivity", "Failed to read value.", error.toException())
                }
            })

             */
            val tipLiveData = dataRepository.getFinancialAdvices()
            //goalsList.clear()
            tipLiveData.observe(this@TipsActivity, Observer { tipsItem ->
                tipsList.clear()
                tipsList.addAll(tipsItem)
                countOfTips = tipsList.size
                binding.tvNumberTips.text = countOfTips.toString()
                adapter.notifyDataSetChanged()
            })
        }


        binding.apply {
            rcView.layoutManager = LinearLayoutManager(this@TipsActivity)
            rcView.adapter = adapter
            val itemDecoration = DividerItemDecoration(this@TipsActivity, LinearLayoutManager.VERTICAL)
            itemDecoration.setDrawable(ContextCompat.getDrawable(this@TipsActivity, R.drawable.shape_indent_rcview)!!)
            rcView.addItemDecoration(itemDecoration)
            navigationView.setNavigationItemSelectedListener {
                when(it.itemId){
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
                }
                true
            }
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
                intent = Intent(this@TipsActivity, IncomeActivity::class.java)

                startActivity(intent)
            }
        }

    }
}

 */