package com.example.myfinanceapplication.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myfinanceapplication.databinding.FragmentBackgroundBinding
import com.example.myfinanceapplication.view.cost.ExpensesActivity
import com.example.myfinanceapplication.view.cost.IncomeActivity
import com.example.myfinanceapplication.view.goals.GoalsActivity

class BackgroundFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentBackgroundBinding.inflate(inflater)
        binding.frameLayout.setOnClickListener {
            Log.d("ClickBackground", "JK")
            if (activity is GoalsActivity) {
                (activity as GoalsActivity).closeFragments()
            }
            if (activity is IncomeActivity) {
                (activity as IncomeActivity).closeFragments()
            }
            if (activity is ExpensesActivity) {
                (activity as ExpensesActivity).closeFragments()
            }

            //(activity as GoalsActivity).infoGoalFragment.closeInfoFragment()
            //parentFragmentManager.popBackStack()
        }
        return binding.root
    }
    fun closeBackgroundFragment(){
        parentFragmentManager.popBackStack()
    }
    companion object {
        @JvmStatic
        fun newInstance() = BackgroundFragment()
    }
}