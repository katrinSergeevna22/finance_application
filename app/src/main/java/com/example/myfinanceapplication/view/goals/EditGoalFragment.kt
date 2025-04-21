package com.example.myfinanceapplication.view.goals

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.myfinanceapplication.viewModel.EditGoalViewModel
import com.example.myfinanceapplication.model.Goal
import com.example.myfinanceapplication.viewModel.GoalViewModel
import com.example.myfinanceapplication.R
import com.example.myfinanceapplication.databinding.FragmentEditGoalBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditGoalFragment : Fragment() {
    lateinit var binding: FragmentEditGoalBinding
    private lateinit var goalViewModel: GoalViewModel
    private lateinit var editGoalViewModel: EditGoalViewModel
    lateinit var selectGoal: Goal
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditGoalBinding.inflate(inflater)
        editGoalViewModel = ViewModelProvider(this).get(EditGoalViewModel::class.java)
        goalViewModel = ViewModelProvider(requireActivity()).get(GoalViewModel::class.java)

        observeSelectGoal()
        setupUI()
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = EditGoalFragment()
    }
    private fun setupUI(){
        binding.apply {
            ibSave.setOnClickListener {
                val title = etTitle.text.toString().trim()
                val sum = etSum.text.toString().replace(" ", "")
                val category = spinnerCategory?.selectedItem.toString()
                val comment = etMultyLineComment.text.toString()

                if (editGoalViewModel.checkData(title, sum, category, comment)) {
                    goalViewModel.setSelectedGoal(editGoalViewModel.selectGoal)
                    (activity as GoalsActivity).closeFragments()
                    //parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(
                        (activity as GoalsActivity),
                        editGoalViewModel.exception,
                        Toast.LENGTH_LONG
                    ).show()
                }
                hideKeyboardFrom((activity as GoalsActivity), requireView())
            }
            ibClose.setOnClickListener{
                hideKeyboardFrom((activity as GoalsActivity), requireView())
                (activity as GoalsActivity).closeFragments()
                //parentFragmentManager.popBackStack()
            }
        }
    }
    fun observeSelectGoal(){
        goalViewModel.selectedGoal.observe(viewLifecycleOwner) { goal ->
            binding.apply {
                etTitle.setText(goal.titleOfGoal)
                etSum.setText(goal.moneyGoal.toString())
                etMultyLineComment.setText(goal.comment)
                selectGoal = goal
                editGoalViewModel.selectGoal = selectGoal
                val categoriesArray = resources.getStringArray(R.array.categoriesGoals)
                val categoryToSet = goal.category

                val categoryIndex = categoriesArray.indexOf(categoryToSet)
                if (categoryIndex != -1) {
                    spinnerCategory?.setSelection(categoryIndex)
                }
            }
        }
    }
    fun editGoal() {
        binding.apply {
            val title = etTitle.text.toString()
            val sum: Long = etSum.text.toString().toLong()
            val category = spinnerCategory?.selectedItem.toString()
            val comment = etMultyLineComment.text.toString()
            val date: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            //val date = editTextDate.text.toString()

            if (title.isNotEmpty() && sum != 0L && category.isNotEmpty()/* && date.isNotEmpty() */) {
                val newGoalData = mapOf(
                    "goalId" to selectGoal.goalId,
                    "titleOfGoal" to  title,
                    "moneyGoal" to sum,
                    "progressOfMoneyGoal" to selectGoal.progressOfMoneyGoal,
                    "date" to selectGoal.date,
                    "category" to category,
                    "comment" to comment,
                    "status" to selectGoal.status.toString(),
                )
                selectGoal.titleOfGoal = title
                selectGoal.moneyGoal = sum
                selectGoal.category = category
                selectGoal.comment = comment
                goalViewModel.setSelectedGoal(selectGoal)

                //val goal = Goal("", title, sum, 0, date, category, comment)
                editGoalViewModel.editGoalToBase(newGoalData, selectGoal)
                parentFragmentManager.popBackStack()
                //fragmentManager?.popBackStack()
            } else {
                // Обработка ошибок
                Log.d("Fragment", "!")
            }
        }
    }
    fun hideKeyboardFrom(context: Context, view: View){
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}