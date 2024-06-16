package com.example.myfinanceapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.myfinanceapplication.databinding.FragmentInfoGoalBinding
import com.example.myfinanceapplication.databinding.FragmentInfoIncomeBinding

class InfoIncomeFragment : Fragment() {
    lateinit var binding: FragmentInfoIncomeBinding
    lateinit var viewModel: CostViewModel
    val newBackgroundFragment = BackgroundFragment.newInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInfoIncomeBinding.inflate(inflater)

        Log.d("Fragment", "InfoIncome")
        viewModel = ViewModelProvider(requireActivity()).get(CostViewModel::class.java)

        viewModel.selectedCost.observe(viewLifecycleOwner) { cost ->
            binding.apply {
                tvTitleInfo.text = cost.titleOfCost
                tvMoneyIncomeInfo.text = cost.moneyCost.toString()
                tvDateIncomeInfo.text = cost.date
                tvIncomeCategory.text = cost.category
                tvIncomeComment.text = cost.comment

            }
        }


        binding.ibEdit.setOnClickListener {
            requireFragmentManager().beginTransaction()
                .replace(R.id.backgroundFragment, newBackgroundFragment)
                .addToBackStack(null)
                .commit()

            requireFragmentManager().beginTransaction()
                .replace(R.id.place_holder_addIncomeFragment, EditIncomeFragment.newInstance())
                .addToBackStack(null)
                .commit()
        }

        binding.ibDelete.setOnClickListener {
            val balance = viewModel.getBalanceNow()
            if (viewModel.selectedCost.value!!.moneyCost > balance){
                Toast.makeText((activity as IncomeActivity), "Баланс станет меньше нуля", Toast.LENGTH_SHORT).show()
            }
            else {
                viewModel.deleteIncome()
                (activity as IncomeActivity).closeFragments()
            }
        }

        binding.ibClose.setOnClickListener{
            (activity as IncomeActivity).closeFragments()
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = InfoIncomeFragment()
    }
}