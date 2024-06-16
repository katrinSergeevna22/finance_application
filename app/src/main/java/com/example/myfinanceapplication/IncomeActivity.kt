package com.example.myfinanceapplication

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.adapter.CostAdapter
import com.example.myfinanceapplication.databinding.ActivityIncomeBinding
import kotlinx.coroutines.launch


class IncomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityIncomeBinding
    lateinit var viewModel: CostViewModel
    private lateinit var adapter: CostAdapter
    val newBackgroundFragment = BackgroundFragment.newInstance()
    val infoIncomeFragment = InfoIncomeFragment.newInstance()
    val newAddIncomeFragment = AddIncomeFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[CostViewModel::class.java]
        //viewModel.getBalance()

        lifecycle.coroutineScope.launch {
            viewModel.apply {
                //loadBalance().join()
                loadIncomes()
                loadOneRandomTipLiveData("Income")
                loadOneRandomGoalLiveData()
            }
        }
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.toolbarGoal.setNavigationOnClickListener {
            binding.drawerGoal.openDrawer(GravityCompat.START)
        }

        adapter = CostAdapter {
            viewModel.setSelectedCost(it)

            supportFragmentManager.beginTransaction()
                .replace(R.id.backgroundFragment, newBackgroundFragment)
                .addToBackStack(null)
                .commit()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.place_holder_infoIncomeFragment, infoIncomeFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.apply {
            rcView.layoutManager = LinearLayoutManager(this@IncomeActivity)
            rcView.adapter = adapter

            val itemDecoration =
                DividerItemDecoration(this@IncomeActivity, LinearLayoutManager.VERTICAL)
            itemDecoration.setDrawable(
                ContextCompat.getDrawable(
                    this@IncomeActivity,
                    R.drawable.shape_indent_rcview
                )!!
            )
            rcView.addItemDecoration(itemDecoration)

            ibAddIncome.setOnClickListener {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.backgroundFragment, newBackgroundFragment)
                    .addToBackStack(null)
                    .commit()

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.place_holder_addIncomeFragment, newAddIncomeFragment)
                    .addToBackStack(null)
                    .commit()

            }
            ibMainGoal.setOnClickListener {
                navigateTo("GoalsActivity")
            }
            ibMainTip.setOnClickListener {
                navigateTo("TipsActivity")
            }
            navigationView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_goals -> {
                        navigateTo("GoalsActivity")
                    }

                    R.id.menu_tips -> {
                        navigateTo("TipsActivity")
                    }

                    R.id.menu_home -> {
                        navigateTo("MainActivity")
                    }

                    R.id.menu_expense -> {
                        navigateTo("ExpenseActivity")
                    }

                    R.id.menu_income -> {
                        navigateTo("IncomeActivity")
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
        /*
        viewModel.getBalanceLiveData().observe(this) {balance ->
            Log.d("GetBalanceInObserve", balance.toString())
            binding.tvBalanceIncome.text = balance.toString()
        }

         */
        viewModel.balanceLiveData.observe(this, Observer { balance ->
            binding.tvBalanceIncome.text = balance.toString()
            Log.d("ShowBalance3", viewModel.getBalanceNow().toString())
            Log.d("ShowBalance4", balance.toString())
        })
        viewModel.getIncomeLiveData().observe(this) { incomeList ->
            adapter.submitList(incomeList)
            adapter.notifyDataSetChanged()
        }
        viewModel.getOneRandomGoalLiveData().observe(this) {goal ->
            binding.apply {
                tvTitleGoal.text = goal.titleOfGoal
                if (goal.date == "") {
                    tvMoneyGoal.text = ""
                }else {
                    tvMoneyGoal.text = goal.moneyGoal.toString()
                    val totalAmountToSave = goal.moneyGoal
                    val currentAmountSaved = goal.progressOfMoneyGoal

                    val progress =
                        (currentAmountSaved.toFloat() / totalAmountToSave.toFloat() * 100).toInt()
                    progressBar.progress = progress
                }
            }
        }
        viewModel.getOneRandomTipLiveData().observe(this) {tip->
            binding.tvTitleTip.text = tip.title
        }
    }
    fun closeFragments(){
        supportFragmentManager.popBackStack()
        supportFragmentManager.popBackStack()
    }
    fun navigateTo(nameActivity: String) {
        when (nameActivity) {
            "GoalsActivity" -> {
                intent = Intent(this@IncomeActivity, GoalsActivity::class.java)
                startActivity(intent)
            }

            "TipsActivity" -> {
                intent = Intent(this@IncomeActivity, TipsActivity::class.java)
                startActivity(intent)
            }

            "MainActivity" -> {
                intent = Intent(this@IncomeActivity, MainActivity::class.java)
                startActivity(intent)
            }
            "IncomeActivity" -> {
                intent = Intent(this@IncomeActivity, IncomeActivity::class.java)
                startActivity(intent)
            }

            "ExpenseActivity" -> {
                intent = Intent(this@IncomeActivity, ExpensesActivity::class.java)
                startActivity(intent)
            }
            "AuthActivity" -> {
                AuthViewModel().exit()
                intent = Intent(this@IncomeActivity, AuthActivity::class.java)
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
/*
class IncomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityIncomeBinding
    private var incomeList: MutableList<Cost> = mutableListOf()
    private val dataRepository = DataRepository()
    private var balance = 0.0
    lateinit var viewModel: CostViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(CostViewModel::class.java)

        binding.toolbarGoal.setNavigationOnClickListener {
            binding.drawerGoal.openDrawer(GravityCompat.START)
        }
    }

    override fun onStart() {
        super.onStart()

        Log.d("ShowBalance1", viewModel.getBalanceNow().toString())
        //tvBalanceIncome.text = viewModel.getBalanceNow().toString()
        //binding.tvBalanceIncome.text = viewModel.balance.toString()
        Log.d("ShowBalance2", viewModel.getBalanceNow().toString())


        var adapter = CostAdapter {
            viewModel.setSelectedCost(it)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.place_holder_infoIncomeFragment, InfoIncomeFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
        adapter.submitList(incomeList)

        lifecycle.coroutineScope.launch {
            // Слушатель для получения списка целей из базы данных
            val incomeLiveData = dataRepository.getIncomes()
            //goalsList.clear()
            incomeLiveData.observe(this@IncomeActivity, Observer { incomeItem ->
                incomeList.clear()
                incomeList.addAll(incomeItem)
                //countOfIncome = incomeList.size
                //binding.tvIncome.text = countOfIncome.toString()
                adapter.notifyDataSetChanged()
            })

            /*
            val incomeRef = database.getReference("users").child(userId).child("income")
            incomeRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    incomeList.clear()

                    for (incomeSnapshot in snapshot.children) {
                        val income = incomeSnapshot.getValue(Cost::class.java)
                        income?.let {
                            incomeList.add(it)
                        }
                    }

                    countOfIncome = incomeList.size
                    binding.tvIncome.text = countOfIncome.toString()
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("IncomeActivity", "Failed to read value.", error.toException())
                }
            })
             */
        }

        binding.apply {
            rcView.layoutManager = LinearLayoutManager(this@IncomeActivity)
            rcView.adapter = adapter

            val itemDecoration = DividerItemDecoration(this@IncomeActivity, LinearLayoutManager.VERTICAL)
            itemDecoration.setDrawable(ContextCompat.getDrawable(this@IncomeActivity, R.drawable.shape_indent_rcview)!!)
            rcView.addItemDecoration(itemDecoration)

            val oneRandomGoalLiveData = dataRepository.getOneGoal()
            oneRandomGoalLiveData.observe(this@IncomeActivity, Observer { goal ->
                tvTitleGoal.text = goal.titleOfGoal
                tvMoneyGoal.text = goal.moneyGoal.toString()
                val totalAmountToSave =  goal.moneyGoal
                val currentAmountSaved = goal.progressOfMoneyGoal

                val progress = (currentAmountSaved.toFloat() / totalAmountToSave.toFloat() * 100).toInt()
                progressBar.progress = progress
            })

            val oneRandomTipLiveData = dataRepository.getOneTip("Income")
            oneRandomTipLiveData.observe(this@IncomeActivity, Observer { tip ->
                tvTitleTip.text = tip.title
            })

            navigationView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_goals -> {
                        navigateTo("GoalsActivity")
                    }

                    R.id.menu_tips -> {
                        navigateTo("TipsActivity")
                    }

                    R.id.menu_home -> {
                        navigateTo("MainActivity")
                    }

                    R.id.menu_expense -> {
                        navigateTo("ExpenseActivity")
                    }

                    R.id.menu_income -> {
                        navigateTo("IncomeActivity")
                    }
                    R.id.menu_exit -> {
                        navigateTo("AuthActivity")
                    }
                }
                true
            }

        }
        binding.ibAddIncome.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.place_holder_addIncomeFragment, AddIncomeFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
        viewModel.balanceLiveData.observe(this, Observer { balance ->
            binding.tvBalanceIncome.text = balance.toString()
            Log.d("ShowBalance3", viewModel.getBalanceNow().toString())
            Log.d("ShowBalance4", balance.toString())
        })

    }

    fun saveIncomeToBase(newCost: Cost){
        dataRepository.writeIncomeData(newCost)
        val balance = viewModel.getBalanceNow()
        Log.d("Balance", balance.toString())
        Log.d("Balance2", newCost.moneyCost.toString())
        Log.d("Balance3", (newCost.moneyCost.toDouble() + balance).toString())
        dataRepository.updateUserBalance(newCost.moneyCost.toDouble() + balance)
    }

    fun getBalance() : Double{
        dataRepository.getUserBalance().observe(this@IncomeActivity, Observer {
            balance = it
            Log.d("GetBalance", balance.toString())
            binding.tvBalanceIncome.text = balance.toString()
        })
        return balance
    }

    fun editIncomeToBase(newIncome: Map<String, Any>, selectIncome: Cost) {
        val newSum = newIncome.get("moneyCost")
        if (newSum != selectIncome.moneyCost){
            val diff = newSum.toString().toDouble() - selectIncome.moneyCost.toString().toDouble()
            val balance = viewModel.getBalanceNow()
            dataRepository.updateUserBalance(balance + diff)
        }
        selectIncome!!.titleOfCost = newIncome["titleOfCost"].toString()
        selectIncome!!.moneyCost = newIncome["moneyCost"].toString().toLong()
        selectIncome!!.category = newIncome["category"].toString()
        selectIncome.comment = newIncome["comment"].toString()
        viewModel.setSelectedCost(selectIncome!!)
        dataRepository.editIncomeToBase(newIncome, selectIncome)

        /*
        database.getReference("users").child(userId).child("income").child(selectIncome.costId)
            .updateChildren(newIncome)
            .addOnSuccessListener {
                Toast.makeText(this, "Данные обновлены", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {
                Toast.makeText(this, "Ошибка обновления данных", Toast.LENGTH_SHORT).show()
            }

 */
    }
    fun deleteIncome(){
        val balance = viewModel.getBalanceNow()
        val sum = viewModel.selectedCost.value?.moneyCost
        if (sum != null){
            dataRepository.updateUserBalance(balance - sum)
            dataRepository.deleteIncome(viewModel.selectedCost.value)
        }
    }

    fun navigateTo(nameActivity: String) {
        when (nameActivity) {
            "GoalsActivity" -> {
                intent = Intent(this@IncomeActivity, GoalsActivity::class.java)
                startActivity(intent)
            }

            "TipsActivity" -> {
                intent = Intent(this@IncomeActivity, TipsActivity::class.java)
                startActivity(intent)
            }

            "MainActivity" -> {
                intent = Intent(this@IncomeActivity, MainActivity::class.java)
                startActivity(intent)
            }
            "IncomeActivity" -> {
                intent = Intent(this@IncomeActivity, IncomeActivity::class.java)
                startActivity(intent)
            }

            "ExpenseActivity" -> {
                intent = Intent(this@IncomeActivity, ExpensesActivity::class.java)
                startActivity(intent)
            }
            "AuthActivity" -> {
                intent = Intent(this@IncomeActivity, AuthActivity::class.java)
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
 */

/*
class IncomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityIncomeBinding
    private var incomeList: MutableList<Cost> = mutableListOf()
    private val dataRepository = DataRepository()
    private var balance = 0.0
    lateinit var viewModel: CostViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(CostViewModel::class.java)
        binding.toolbarGoal.setNavigationOnClickListener {
            binding.drawerGoal.openDrawer(GravityCompat.START)
        }
    }

    override fun onStart() {
        super.onStart()

        var adapter = CostAdapter {
            viewModel.setSelectedCost(it)
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.place_holder_infoIncomeFragment, InfoIncomeFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
        adapter.submitList(incomeList)

        lifecycle.coroutineScope.launch {
            // Слушатель для получения списка целей из базы данных
            val incomeLiveData = dataRepository.getIncomes()
            //goalsList.clear()
            incomeLiveData.observe(this@IncomeActivity, Observer { incomeItem ->
                incomeList.clear()
                incomeList.addAll(incomeItem)
                //countOfIncome = incomeList.size
                //binding.tvIncome.text = countOfIncome.toString()
                adapter.notifyDataSetChanged()
            })

            /*
            val incomeRef = database.getReference("users").child(userId).child("income")
            incomeRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    incomeList.clear()

                    for (incomeSnapshot in snapshot.children) {
                        val income = incomeSnapshot.getValue(Cost::class.java)
                        income?.let {
                            incomeList.add(it)
                        }
                    }

                    countOfIncome = incomeList.size
                    binding.tvIncome.text = countOfIncome.toString()
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("IncomeActivity", "Failed to read value.", error.toException())
                }
            })
             */
        }

        binding.apply {
            rcView.layoutManager = LinearLayoutManager(this@IncomeActivity)
            rcView.adapter = adapter

            val itemDecoration = DividerItemDecoration(this@IncomeActivity, LinearLayoutManager.VERTICAL)
            itemDecoration.setDrawable(ContextCompat.getDrawable(this@IncomeActivity, R.drawable.shape_indent_rcview)!!)
            rcView.addItemDecoration(itemDecoration)

            val oneRandomGoalLiveData = dataRepository.getOneGoal()
            oneRandomGoalLiveData.observe(this@IncomeActivity, Observer { goal ->
                tvTitleGoal.text = goal.titleOfGoal
                tvMoneyGoal.text = goal.moneyGoal.toString()
                val totalAmountToSave =  goal.moneyGoal
                val currentAmountSaved = goal.progressOfMoneyGoal

                val progress = (currentAmountSaved.toFloat() / totalAmountToSave.toFloat() * 100).toInt()
                progressBar.progress = progress
            })

            val oneRandomTipLiveData = dataRepository.getOneTip("Income")
            oneRandomTipLiveData.observe(this@IncomeActivity, Observer { tip ->
                tvTitleTip.text = tip.title
            })

            navigationView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_goals -> {
                        navigateTo("GoalsActivity")
                    }

                    R.id.menu_tips -> {
                        navigateTo("TipsActivity")
                    }

                    R.id.menu_home -> {
                        navigateTo("MainActivity")
                    }

                    R.id.menu_expense -> {
                        navigateTo("ExpenseActivity")
                    }

                    R.id.menu_income -> {
                        navigateTo("IncomeActivity")
                    }
                    R.id.menu_exit -> {
                        navigateTo("AuthActivity")
                    }
                }
                true
            }

        }
        binding.ibAddIncome.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.place_holder_addIncomeFragment, AddIncomeFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

    }

    fun saveIncomeToBase(newCost: Cost){
        dataRepository.writeIncomeData(newCost)
        val balance = viewModel.getBalanceNow()
        Log.d("Balance", balance.toString())
        Log.d("Balance2", newCost.moneyCost.toString())
        Log.d("Balance3", (newCost.moneyCost.toDouble() + balance).toString())
        dataRepository.updateUserBalance(newCost.moneyCost.toDouble() + balance)
    }

    fun getBalance() : Double{
        dataRepository.getUserBalance().observe(this@IncomeActivity, Observer {
            balance = it
            Log.d("GetBalance", balance.toString())
            binding.tvBalanceIncome.text = balance.toString()
        })
        return balance
    }

    fun editIncomeToBase(newIncome: Map<String, Any>, selectIncome: Cost) {
        val newSum = newIncome.get("moneyCost")
        if (newSum != selectIncome.moneyCost){
            val diff = newSum.toString().toDouble() - selectIncome.moneyCost.toString().toDouble()
            val balance = viewModel.getBalanceNow()
            dataRepository.updateUserBalance(balance + diff)
        }
        selectIncome!!.titleOfCost = newIncome["titleOfCost"].toString()
        selectIncome!!.moneyCost = newIncome["moneyCost"].toString().toLong()
        selectIncome!!.category = newIncome["category"].toString()
        selectIncome.comment = newIncome["comment"].toString()
        viewModel.setSelectedCost(selectIncome!!)
        dataRepository.editIncomeToBase(newIncome, selectIncome)

        /*
        database.getReference("users").child(userId).child("income").child(selectIncome.costId)
            .updateChildren(newIncome)
            .addOnSuccessListener {
                Toast.makeText(this, "Данные обновлены", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {
                Toast.makeText(this, "Ошибка обновления данных", Toast.LENGTH_SHORT).show()
            }

 */
    }
    fun deleteIncome(){
        val balance = viewModel.getBalanceNow()
        val sum = viewModel.selectedCost.value?.moneyCost
        if (sum != null){
            dataRepository.updateUserBalance(balance - sum)
            dataRepository.deleteIncome(viewModel.selectedCost.value)
        }
    }

    fun navigateTo(nameActivity: String) {
        when (nameActivity) {
            "GoalsActivity" -> {
                intent = Intent(this@IncomeActivity, GoalsActivity::class.java)
                startActivity(intent)
            }

            "TipsActivity" -> {
                intent = Intent(this@IncomeActivity, TipsActivity::class.java)
                startActivity(intent)
            }

            "MainActivity" -> {
                intent = Intent(this@IncomeActivity, MainActivity::class.java)
                startActivity(intent)
            }
            "IncomeActivity" -> {
                intent = Intent(this@IncomeActivity, IncomeActivity::class.java)
                startActivity(intent)
            }

            "ExpenseActivity" -> {
                intent = Intent(this@IncomeActivity, ExpensesActivity::class.java)
                startActivity(intent)
            }
            "AuthActivity" -> {
                intent = Intent(this@IncomeActivity, AuthActivity::class.java)
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

 */

