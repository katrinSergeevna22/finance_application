package com.example.myfinanceapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.myfinanceapplication.databinding.FragmentInfoGoalBinding

class InfoGoalFragment : Fragment() {
    lateinit var binding: FragmentInfoGoalBinding
    private lateinit var viewModel: GoalViewModel
    private lateinit var editViewModel: EditGoalViewModel
    private val backgroundFragment = BackgroundFragment.newInstance()
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInfoGoalBinding.inflate(inflater)

        viewModel = ViewModelProvider(requireActivity()).get(GoalViewModel::class.java)
        editViewModel = ViewModelProvider(requireActivity()).get(EditGoalViewModel::class.java)
        viewModel.selectedGoal.observe(viewLifecycleOwner) { goal ->
            binding.apply {
                tvTitleInfo.text = goal.titleOfGoal
                tvMoneyGoalInfo.text = goal.moneyGoal.toString()
                tvDateInfo.text = goal.date
                tvProgressMoneyInfo.text = goal.progressOfMoneyGoal.toString() + " из " + goal.moneyGoal.toString()
                tvGoalCategory.text = goal.category
                tvGoalComment.text = goal.comment

                val totalAmountToSave =  goal.moneyGoal// Общая сумма для накопления
                val currentAmountSaved = goal.progressOfMoneyGoal // Текущая сумма, которая уже накоплена
                val progress = (currentAmountSaved.toFloat() / totalAmountToSave.toFloat() * 100).toInt()
                progressBarInfo.progress = progress
            }
        }

        binding.apply {
            when (viewModel.getSelectedCategory().value){
                "Active" -> {
                }
                "Deleted" -> {
                    ibAddMoney.visibility = View.GONE
                    tvAddMoney.visibility = View.GONE
                    //tvDelete.text = "Восстановить"
                    tvDelete.text = "Восстановить"
                    tvEdit.text = "Удалить"
                }
                "Achieved" -> {
                    ibAddMoney.visibility = View.GONE
                    tvAddMoney.visibility = View.GONE
                    //ibEdit.visibility = View.GONE
                    //tvEdit.visibility = View.GONE
                    //ibDelete.visibility = View.GONE
                    //tvDelete.visibility = View.GONE
                }
            }

            ibEdit.setOnClickListener {
                if (viewModel.getSelectedCategory().value == "Deleted"){
                    viewModel.deleteGoal()
                    (activity as GoalsActivity).closeFragments()
                }
                else {
                    requireFragmentManager().beginTransaction()
                        .replace(R.id.backgroundFragment, backgroundFragment)
                        .addToBackStack(null)
                        .commit()
                    requireFragmentManager().beginTransaction()
                        .replace(R.id.place_holder_addFragment, EditGoalFragment.newInstance())
                        .addToBackStack(null)
                        .commit()
                }
            }
            /*
            ibAddMoney.setOnClickListener {
                /*
                val intent = Intent((activity as GoalsActivity), ExpensesActivity::class.java)
                intent.putExtra("AddExpense", viewModel.selectedGoal.value!!.titleOfGoal)
                startActivity(intent)

                 */
                requireFragmentManager().beginTransaction()
                    .replace(R.id.place_holder_addFragment, AddExpenseFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
            }

             */
            ibDelete.setOnClickListener {
                val selectGoal = viewModel.selectedGoal.value
                var newStatus = ""
                if (viewModel.getSelectedCategory().value == "Deleted"){
                    if (selectGoal?.moneyGoal == selectGoal?.progressOfMoneyGoal){
                        newStatus = "Achieved"
                    }else {
                        newStatus = "Active"
                    }
                }
                else{
                    newStatus = "Deleted"
                }
                val newGoalData = mapOf(
                    "goalId" to selectGoal!!.goalId,
                    "titleOfGoal" to selectGoal.titleOfGoal.toString(),
                    "moneyGoal" to selectGoal.moneyGoal,
                    "progressOfMoneyGoal" to selectGoal.progressOfMoneyGoal,
                    "date" to selectGoal.date,
                    "category" to selectGoal.category.toString(),
                    "comment" to selectGoal.comment.toString(),
                    "status" to newStatus,
                )
                selectGoal.status = newStatus
                editViewModel.editGoalToBase(newGoalData, selectGoal)
                viewModel.fragmentIsOpen = false

                (activity as GoalsActivity).closeFragments()
            }
            ibClose.setOnClickListener {
                //viewModel.fragmentIsOpen = false
                //(activity as GoalsActivity).binding.backgroundFragment.visibility = View.GONE

                (activity as GoalsActivity).closeFragments()
                //(activity as GoalsActivity).backgroundFragment.closeBackgroundFragment()
                //closeInfoFragment()
            }
        }
        /*
        (activity as GoalsActivity).binding.backgroundFragment.setOnClickListener { Log.d("Click", "")
            parentFragmentManager.popBackStack() }

         */
        /*
        (activity as GoalsActivity).binding.backgroundFragment. .placeHolderInfoFragment.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val rect = Rect()
                this.view?.getGlobalVisibleRect(rect)
                if (!rect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    // Касание произошло за пределами фрагмента
                    fragmentManager?.beginTransaction()?.remove(this)?.commit()

                }
            }
            true
        }

         */
        return binding.root

    }
    fun closeInfoFragment(){
        this.parentFragmentManager.popBackStack()
    }
    companion object {
        @JvmStatic
        fun newInstance() = InfoGoalFragment()
    }

}