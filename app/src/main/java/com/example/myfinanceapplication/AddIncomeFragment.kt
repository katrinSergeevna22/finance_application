package com.example.myfinanceapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.myfinanceapplication.databinding.FragmentAddIncomeBinding

class AddIncomeFragment : Fragment() {
    lateinit var binding: FragmentAddIncomeBinding
    private lateinit var viewModel: CostViewModel
    private lateinit var addCostViewModel: AddCostViewModel
    var selectIncome: Cost = Cost()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddIncomeBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(CostViewModel::class.java)
        addCostViewModel = ViewModelProvider(this).get(AddCostViewModel::class.java)
        setupUI()

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddIncomeFragment()
    }

    fun setupUI(){
        binding.ibSave.setOnClickListener {
            binding.apply {
                val title = etTitle.text.toString().trim()
                val sum = etSum.text.toString().replace(" ", "")
                val category = spinnerCategory.selectedItem.toString()
                val comment = etMultyLineComment.text.toString()
                if (addCostViewModel.checkDataIncome(title, sum, category, comment, viewModel.getBalanceNow())){
                    (activity as IncomeActivity).closeFragments()
                }
                else{
                    // Обработка ошибок
                    Toast.makeText((activity as IncomeActivity), "Заполните все необходимые поля", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.ibClose.setOnClickListener{
            (activity as IncomeActivity).closeFragments()
        }
    }

}