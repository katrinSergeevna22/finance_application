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
        //Log.d("EditIncomeFr2", (activity as IncomeActivity).itsEdit.toString())

        /*
        if ((activity as IncomeActivity).itsEdit){
            Log.d("EditIncomeFr3", viewModel.selectedCost.value.toString())
            viewModel.selectedCost.observe(viewLifecycleOwner) {
                selectIncome = it
                Log.d("EditIncomeFr3", (activity as IncomeActivity).itsEdit.toString())
                Log.d("EditIncomeFr3", selectIncome.toString())
                Log.d("EditIncomeFr3 It", it.toString())
                binding.apply {
                    etTitle.setText(selectIncome!!.titleOfCost.toString())
                    etSum.setText(selectIncome!!.moneyCost.toString())
                    etMultyLineComment.setText(selectIncome!!.comment)

                    val categoriesArray = resources.getStringArray(R.array.categoriesIncome)
                    val categoryToSet = selectIncome.category

                    val categoryIndex = categoriesArray.indexOf(categoryToSet)
                    if (categoryIndex != -1) {
                        spinnerCategory.setSelection(categoryIndex)
                    }
                }
            }
        }
         */
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