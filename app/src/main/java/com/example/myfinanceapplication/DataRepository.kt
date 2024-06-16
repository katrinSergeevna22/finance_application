package com.example.myfinanceapplication

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DataRepository {
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Получение ID текущего пользователя
    private val userId = auth.currentUser!!.uid

    fun getUserBalance() : MutableLiveData<Double> {
        var balance = MutableLiveData<Double>()
        val balanceRef = database.getReference("users").child(userId).child("balance")
        balanceRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var newBalance = snapshot.getValue(Double::class.java)
                if (newBalance == null){
                    newBalance = 0.0
                    updateUserBalance(0.0)
                }
                balance.value = newBalance
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибки
                balance.value = 0.0
            }
        })

        return balance
    }

    fun updateUserBalance(newBalance: Double) {
        val balanceRef = database.getReference("users").child(userId).child("balance")
        balanceRef.setValue(newBalance)
    }

    fun getIncomes(): MutableLiveData<List<Cost>> {
        val incomeLiveData = MutableLiveData<List<Cost>>()

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
            }
        })
        return incomeLiveData
    }

    fun getExpenses(): MutableLiveData<List<Cost>> {
        val expenseLiveData = MutableLiveData<List<Cost>>()

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
                // Обработка ошибок при чтении данных из базы данных
            }
        })
        return expenseLiveData
    }


    fun getGoals(): MutableLiveData<List<Goal>> {
        val financeItemsLiveData = MutableLiveData<List<Goal>>()
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
                    Log.d("DataRepository", "Failed to read value.", error.toException())
                }
            })
            return financeItemsLiveData

    }

    fun getOneGoal() : MutableLiveData<Goal>{
        var oneGoalLiveData = MutableLiveData<Goal>()
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
                var goalActiveList = items.filter { it.status == "Active" }
                if (goalActiveList.isEmpty()){
                    oneGoalLiveData.value = Goal("0","Добавьте цель", 0L, 0L, "", "", "")
                }
                else {
                    oneGoalLiveData.value = goalActiveList.random()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("GoalActivity", "Failed to read value.", error.toException())
            }
        })
        return oneGoalLiveData
    }

    fun getFinancialAdvices(): MutableLiveData<List<Tip>> {
        val tipLiveData = MutableLiveData<List<Tip>>()

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
                Log.d("TipActivity", "Failed to read value.", error.toException())
            }
        })
        return tipLiveData
    }

    fun getOneTip(categoryTip: String = "all") : MutableLiveData<Tip>{
        var oneTipLiveData = MutableLiveData<Tip>()
        val tipsRef = database.getReference().child("tips")
        tipsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<Tip>()
                for (tipSnapshot in snapshot.children) {
                    val tip = tipSnapshot.getValue(Tip::class.java)
                    tip?.let {
                        if (categoryTip == tip.theme || categoryTip == "all"){
                            items.add(it)
                        }
                    }
                }
                oneTipLiveData.value = items.random()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TipActivity", "Failed to read value.", error.toException())
            }
        })
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
        val costRef = database.getReference("users").child(userId).child("income").push()
        val costId = costRef.key
        newCost.costId = costId.toString()
        costRef.setValue(newCost)
    }
    fun editIncomeToBase(newIncome: Map<String, Any>, selectIncome: Cost) {

        database.getReference("users").child(userId).child("income").child(selectIncome.costId)
            .updateChildren(newIncome)
            .addOnSuccessListener {
                //Toast.makeText(this, "Данные обновлены", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {
                //Toast.makeText(this, "Ошибка обновления данных", Toast.LENGTH_SHORT).show()
            }
    }

    fun deleteIncome(selectIncome: Cost?){
        if (selectIncome != null) {
            database.getReference("users").child(userId)
                .child("income")
                .child(selectIncome.costId)
                .removeValue()
        }
    }

    fun writeExpenseData(newCost : Cost) {
        val costRef = database.getReference("users").child(userId).child("expense").push()
        val costId = costRef.key
        newCost.costId = costId.toString()
        costRef.setValue(newCost)
    }

    fun editExpenseToBase(newExpense: Map<String, Any>, selectExpense: Cost) {

        database.getReference("users").child(userId).child("expense").child(selectExpense.costId)
            .updateChildren(newExpense)
            .addOnSuccessListener {
                //Toast.makeText(this, "Данные обновлены", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {
                //Toast.makeText(this, "Ошибка обновления данных", Toast.LENGTH_SHORT).show()
            }
    }

    fun deleteExpense(selectExpense: Cost?){
        if (selectExpense != null) {
            database.getReference("users").child(userId)
                .child("expense")
                .child(selectExpense.costId)
                .removeValue()
        }
    }

    fun writeGoalData(newGoal : Goal) {
        val goalsRef = database.getReference("users").child(userId).child("goals").push()
        val goalId = goalsRef.key
        newGoal.goalId = goalId.toString()
        goalsRef.setValue(newGoal)
    }
    fun editGoalToBase(newGoal: Map<String, Any>, selectGoal: Goal) {
        database.getReference("users").child(userId).child("goals").child(selectGoal.goalId)
            .updateChildren(newGoal)
            .addOnSuccessListener {
                //Toast.makeText(, "Данные обновлены", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {
                //Toast.makeText(this, "Ошибка обновления данных", Toast.LENGTH_SHORT).show()
            }
    }

    fun deleteGoal(selectGoal: Goal){
        if (selectGoal != null) {
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
