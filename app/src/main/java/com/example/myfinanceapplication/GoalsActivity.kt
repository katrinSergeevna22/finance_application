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
            navigateTo("TipsActivity")
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
            adapter.notifyDataSetChanged()
        }

        viewModel.getOneRandomTipLiveData().observe(this) { tip ->
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
/*
class GoalsActivity : AppCompatActivity() {
    lateinit var binding: ActivityGoalsBinding
    private val dataRepository = DataRepository()
    private var goalsList: MutableList<Goal> = mutableListOf()
    private var goalsCategoryList: MutableList<Goal> = mutableListOf()
    private lateinit var viewModel: GoalViewModel
    private var countOfGoals = 0
    private var categoryGoal: String = "Active"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(GoalViewModel::class.java)

        binding.toolbarGoal.setNavigationOnClickListener {
            binding.drawerGoal.openDrawer(GravityCompat.START)
        }
        //setSupportActionBar(binding.toolbarGoal);
    }

    override fun onStart() {
        super.onStart()

        var adapter = GoalAdapter {
            viewModel.setSelectedGoal(it)
            viewModel.fragmentIsOpen = true
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.place_holder_infoFragment, InfoGoalFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }
        Log.d("FindExGoal", "Start")
        adapter.submitList(goalsCategoryList)
        Log.d("FindExGoal", "Submit")

        /*
        binding.toolbarGoal.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.item_active -> viewModel.selectCategory("Active")
                R.id.item_deleted -> viewModel.selectCategory("Deleted")
                R.id.item_achieved -> viewModel.selectCategory("Archived")
            }
            true
        }

        viewModel.goalsByCategory.observe(this) { goals ->
            goalsList.clear()
            goalsList.addAll(goals)
            countOfGoals = goalsList.size
            binding.tvCount.text = countOfGoals.toString()
            adapter.notifyDataSetChanged()
        }

         */
        lifecycle.coroutineScope.launch {
            //Log.d("FindExGoal", "Coroutin")
            // Слушатель для получения списка целей из базы данных
            /*
            val goalsRef = database.getReference("users").child(userId).child("goals")
            goalsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    goalsList.clear()
                    Log.d("FindExGoal", "In Coroutin1")
                    for (goalSnapshot in snapshot.children) {
                        val goal = goalSnapshot.getValue(Goal::class.java)
                        goal?.let {
                            if (it.status == "Active"){
                                goalsList.add(it)
                            }
                        }
                    }
                    Log.d("FindExGoal", "In Coroutin2")
                    //adapter.submitList(goalsList)
                    Log.d("GoalSize", goalsList.size.toString())
                    Log.d("GoalSize", goalsList.toString())
                    countOfGoals = goalsList.size
                    binding.tvCount.text = countOfGoals.toString()
                    Log.d("GoalSize", countOfGoals.toString())
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("MainActivity", "Failed to read value.", error.toException())
                }
            })

             */

            var goalLiveData = dataRepository.getGoals()
            //goalsList.clear()
            goalLiveData.observe(this@GoalsActivity, Observer { goalsItem ->
                goalsList.clear()
                goalsList.addAll(goalsItem)

                goalsCategoryList.clear()
                goalsCategoryList.addAll(goalsList.filter { it.status == categoryGoal }.toMutableList())
                countOfGoals = goalsCategoryList.size
                binding.tvCount.text = countOfGoals.toString()
                adapter.notifyDataSetChanged()
                Log.d("ObserveDataRep", goalsCategoryList.toString())
            })

            viewModel.getSelectedCategory().observe(this@GoalsActivity, Observer { category ->
                // Фильтрация данных по категории и обновление RecyclerView
                goalsCategoryList = goalsList.filter { it.status == category }.toMutableList()
                categoryGoal = category
                Log.d("ObserveGetSelect Category", category)
                Log.d("ObserveGetSelect", goalsCategoryList.toString())

                binding.tvCount.text = goalsCategoryList.size.toString()
                binding.tvTitleGoals.text = getTitleOfCategory()
                adapter.submitList(goalsCategoryList)
                adapter.notifyDataSetChanged()

            })
        }

        binding.apply {
            rcView.layoutManager = LinearLayoutManager(this@GoalsActivity)
            rcView.adapter = adapter
            Log.d("FindExGoal", "Adapter")
            val itemDecoration = DividerItemDecoration(this@GoalsActivity, LinearLayoutManager.VERTICAL)
            itemDecoration.setDrawable(ContextCompat.getDrawable(this@GoalsActivity, R.drawable.shape_indent_rcview)!!)
            rcView.addItemDecoration(itemDecoration)
            Log.d("FindExGoal", "End")

            val oneRandomTipLiveData = dataRepository.getOneTip("Goal")
            oneRandomTipLiveData.observe(this@GoalsActivity, Observer { tip ->
                tvTitleTip.text = tip.title
                tvAdvice.text = tip.text
            })

            //tvCount.text = countOfGoals.toString()

            tvTitleGoals.text = getTitleOfCategory()
            //setSupportActionBar(binding.toolbarGoal)

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
                when (item.itemId) {
                    R.id.item_active -> viewModel.setSelectedCategory("Active")
                    R.id.item_achieved -> viewModel.setSelectedCategory("Achieved")
                    R.id.item_deleted -> viewModel.setSelectedCategory("Deleted")
                }
                true
            }

            //binding.toolbarGoal.inflateMenu(R.menu.status_goals_menu)


        }
        binding.ibAddGoal.setOnClickListener {
            Log.d("FindExGoal", "FragmentClick")
            viewModel.fragmentIsOpen = true
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.place_holder_addFragment, AddGoalFragment.newInstance())
                .addToBackStack(null)
                .commit()



            //val newGoal = Goal(3, "Квартира", 5000000, 0, Date(1)," ", " ")


            /*
            val goalName = etGoalName.text.toString()
            val goalDescription = etGoalDescription.text.toString()

            if (goalName.isNotEmpty() && goalDescription.isNotEmpty()) {
                val goalsRef = database.getReference("users").child(userId).child("goals").push()
                val goalId = goalsRef.key

                val newGoal = Goal(goalId, goalName, goalDescription)
                goalsRef.setValue(newGoal)

                Toast.makeText(this, "Цель успешно добавлена", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            }

             */

            /*val goal = Goal(2, "Машина", 500000, 0, Date(1)," ", " ")
            dataRepository.writeGoalData(goal)

             */
        }
    }
/*
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.status_goals_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d("ItemSelection", item.toString())
        when (item.itemId) {
            R.id.item_active -> viewModel.setSelectedCategory("Active")
            R.id.item_achieved -> viewModel.setSelectedCategory("Achieved")
            R.id.item_deleted -> viewModel.setSelectedCategory("Deleted")
        }
        return super.onOptionsItemSelected(item)
    }

 */

    private fun observeViewModel() {
        viewModel.getGoalsLiveData().observe(this) { goalList ->
            adapter.submitList(goalList)
            binding.tvCount.text = goalList.size.toString()
            adapter.notifyDataSetChanged()
        }
    }
    fun getTitleOfCategory() : String{
        when (categoryGoal){
            "Active" -> return "Активные цели"
            "Deleted" -> return "Архивные цели"
            "Achieved" -> return "Достигнутые цели"
        }
        return "Цели"
    }

    fun editGoalToBase(newGoal: Map<String, Any>, selectGoal: Goal){

        dataRepository.editGoalToBase(newGoal, selectGoal)
        //val newGoal = Goal(2, "Машина", 500000, 0, "1.01.2001"," ", " ")
        //val goalsRef = database.getReference("users").child(userId).child("goals").push()
        //val goalId = goalsRef.key

        //goalsRef.setValue(newGoal)

        /*database.getReference("users").child(userId).child("goals").child(selectGoal.goalId).updateChildren(newGoal)
            .addOnSuccessListener {
                Toast.makeText(this, "Данные обновлены", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {
                Toast.makeText(this, "Ошибка обновления данных", Toast.LENGTH_SHORT).show()
            }

         */

/*
        database.getReference("users").child(userId).child("goals").child(newGoal.goalId).setValue(newGoal)
            .addOnSuccessListener {
                Toast.makeText(this, "Данные обновлены", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {
                Toast.makeText(this, "Ошибка обновления данных", Toast.LENGTH_SHORT).show()
            }

 */
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

            "IncomeActivity" -> {
                intent = Intent(this@GoalsActivity, IncomeActivity::class.java)

                startActivity(intent)
            }

            "ExpenseActivity" -> {
                intent = Intent(this@GoalsActivity, ExpensesActivity::class.java)

                startActivity(intent)
            }
            "AuthActivity"-> {
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

 */