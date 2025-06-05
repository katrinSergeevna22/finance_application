package com.example.myfinanceapplication.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DataRepository {
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private const val TAG = "DataRepository"
    }

    // Получение ID текущего пользователя
    private val userId = auth.currentUser?.uid

    private val balanceLiveData = MutableLiveData<Double>()
    private var balanceListener: ValueEventListener? = null

    fun getUserBalance(): LiveData<Double> {
        userId?.let { uid ->
            // Удаляем предыдущий listener, если был
            balanceListener?.let { listener ->
                database.getReference("users").child(uid).child("balance")
                    .removeEventListener(listener)
            }

            val balanceRef = database.getReference("users").child(uid).child("balance")
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val newBalance = snapshot.getValue(Double::class.java) ?: run {
                        updateUserBalance(0.0)
                        0.0
                    }
                    balanceLiveData.postValue(newBalance)
                    Log.d("BalanceRepo", "Balance updated: $newBalance")
                }

                override fun onCancelled(error: DatabaseError) {
                    balanceLiveData.postValue(0.0)
                    Log.e("BalanceRepo", "Failed to read balance: ${error.message}")
                }
            }

            balanceListener = listener
            balanceRef.addValueEventListener(listener) // Постоянный listener
        } ?: run {
            balanceLiveData.postValue(0.0)
        }

        return balanceLiveData
    }

    fun cleanup() {
        userId?.let { uid ->
            balanceListener?.let { listener ->
                database.getReference("users").child(uid).child("balance")
                    .removeEventListener(listener)
            }
        }
    }

    fun updateUserBalance(newBalance: Double) {
        userId?.let { uid ->
            database.getReference("users").child(uid).child("balance")
                .setValue(newBalance)
        }
    }

    fun getIncomes(): MutableLiveData<List<Cost>> {
        val incomeLiveData = MutableLiveData<List<Cost>>()

        if (userId != null) {
            val incomeRef = database.getReference("users").child(userId).child("income")
            incomeRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = mutableListOf<Cost>()
                    for (incomeSnapshot in snapshot.children) {
                        val income = incomeSnapshot.getValue(Cost::class.java)
                        income?.let {
                            items.add(it)
                        }
                    }
                    incomeLiveData.value = items
                }

                override fun onCancelled(error: DatabaseError) {
                    // Обработка ошибок при чтении данных из базы данных
                    Log.e(TAG, "Failed to read value.", error.toException())
                }
            })
        }
        return incomeLiveData
    }

    fun getExpenses(): MutableLiveData<List<Cost>> {
        val expenseLiveData = MutableLiveData<List<Cost>>()

        if (userId != null) {
            val expenseRef = database.getReference("users").child(userId).child("expense")
            expenseRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = mutableListOf<Cost>()
                    for (expenseSnapshot in snapshot.children) {
                        val expenses = expenseSnapshot.getValue(Cost::class.java)
                        expenses?.let {
                            items.add(it)
                        }
                    }
                    expenseLiveData.value = items
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Failed to read value.", error.toException())
                }
            })
        }
        return expenseLiveData
    }


    fun getGoals(): MutableLiveData<List<Goal>> {
        val financeItemsLiveData = MutableLiveData<List<Goal>>()
        if (userId != null) {
            val goalsRef = database.getReference("users").child(userId).child("goals")
            goalsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = mutableListOf<Goal>()
                    for (goalSnapshot in snapshot.children) {
                        val goal = goalSnapshot.getValue(Goal::class.java)
                        goal?.let {
                            items.add(it)
                        }
                    }
                    financeItemsLiveData.value = items
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Failed to read value.", error.toException())
                }
            })
        }
        return financeItemsLiveData

    }

    fun getOneGoal(): MutableLiveData<Goal> {
        val oneGoalLiveData = MutableLiveData<Goal>()
        if (userId != null) {
            val goalsRef = database.getReference("users").child(userId).child("goals")
            goalsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = mutableListOf<Goal>()
                    for (goalSnapshot in snapshot.children) {
                        val goal = goalSnapshot.getValue(Goal::class.java)
                        goal?.let {
                            items.add(it)
                        }
                    }
                    val goalActiveList = items.filter { it.status == "Active" }
                    if (goalActiveList.isEmpty()) {
                        oneGoalLiveData.value = Goal("0", "Добавьте цель", 0L, 0L, "", "", "")
                    } else {
                        oneGoalLiveData.value = goalActiveList.random()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "Failed to read value.", error.toException())
                }
            })
        }
        return oneGoalLiveData
    }

    fun getFinancialAdvices(): MutableLiveData<List<Tip>> {
        val tipLiveData = MutableLiveData<List<Tip>>()
        if (userId != null) {
            val tipsRef = database.getReference().child("tips")
            tipsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = mutableListOf<Tip>()
                    for (tipSnapshot in snapshot.children) {
                        val tip = tipSnapshot.getValue(Tip::class.java)
                        tip?.let {
                            items.add(it)
                        }
                    }
                    tipLiveData.value = items
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "Failed to read value.", error.toException())
                }
            })
        }
        return tipLiveData
    }

    fun getOneTip(categoryTip: String = "all"): MutableLiveData<Tip> {
        val oneTipLiveData = MutableLiveData<Tip>()
        if (userId != null) {
            val tipsRef = database.getReference().child("tips")
            tipsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = mutableListOf<Tip>()
                    for (tipSnapshot in snapshot.children) {
                        val tip = tipSnapshot.getValue(Tip::class.java)
                        tip?.let {
                            if (categoryTip == tip.theme || categoryTip == "all") {
                                items.add(it)
                            }
                        }
                    }
                    oneTipLiveData.value = items.random()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "Failed to read value.", error.toException())
                }
            })
        }
        return oneTipLiveData
    }
    /*
        fun writeUser(login: String, password: String) {
            //val userId = database.child("users").push().key
            val newUser = User(login, password)
            userId = login
            val q = database.child("users").child(login).setValue(newUser)
            Log.d("BaseUser", q.isSuccessful.toString())
        }
        */

    fun writeIncomeData(newCost: Cost) {
        if (userId != null) {
            val costRef = database.getReference("users").child(userId).child("income").push()
            val costId = costRef.key
            newCost.costId = costId.toString()
            costRef.setValue(newCost)
        }
    }

    fun editIncomeToBase(newIncome: Map<String, Any>, selectIncome: Cost) {
        if (userId != null) {
            database.getReference("users").child(userId).child("income").child(selectIncome.costId)
                .updateChildren(newIncome)
                .addOnSuccessListener {
                    //Toast.makeText(this, "Данные обновлены", Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener {
                    //Toast.makeText(this, "Ошибка обновления данных", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun deleteIncome(selectIncome: Cost?) {
        if (selectIncome != null && userId != null) {
            database.getReference("users").child(userId)
                .child("income")
                .child(selectIncome.costId)
                .removeValue()
        }
    }

    fun writeExpenseData(newCost: Cost) {
        if (userId != null) {
            val costRef = database.getReference("users").child(userId).child("expense").push()
            val costId = costRef.key
            newCost.costId = costId.toString()
            costRef.setValue(newCost)
        }
    }

    fun editExpenseToBase(newExpense: Map<String, Any>, selectExpense: Cost) {
        if (userId != null) {
            database.getReference("users").child(userId).child("expense")
                .child(selectExpense.costId)
                .updateChildren(newExpense)
                .addOnSuccessListener {
                    //Toast.makeText(this, "Данные обновлены", Toast.LENGTH_SHORT).show()

                }
                .addOnFailureListener {
                    //Toast.makeText(this, "Ошибка обновления данных", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun deleteExpense(selectExpense: Cost?) {
        if (selectExpense != null && userId != null) {
            database.getReference("users").child(userId)
                .child("expense")
                .child(selectExpense.costId)
                .removeValue()
        }
    }

    fun writeGoalData(newGoal: Goal) {
        if (userId != null) {
            val goalsRef = database.getReference("users").child(userId).child("goals").push()
            val goalId = goalsRef.key
            newGoal.goalId = goalId.toString()
            goalsRef.setValue(newGoal)
        }
    }

    fun editGoalToBase(newGoal: Map<String, Any>, selectGoal: Goal) {
        if (userId != null) {
            database.getReference("users").child(userId).child("goals").child(selectGoal.goalId)
                .updateChildren(newGoal)
                .addOnSuccessListener {
                    //Toast.makeText(, "Данные обновлены", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    //Toast.makeText(this, "Ошибка обновления данных", Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun deleteGoal(selectGoal: Goal?) {
        if (selectGoal != null && userId != null) {
            database.getReference("users").child(userId)
                .child("goals")
                .child(selectGoal.goalId)
                .removeValue()
        }
    }
    /*
        fun writeFinancialAdviceData(advice: Tip) {
            val adviceId = database.child("users").child("financial_advices").push().key
            database.child("financial_advices").child(adviceId!!).setValue(advice)
        }

         */
}
