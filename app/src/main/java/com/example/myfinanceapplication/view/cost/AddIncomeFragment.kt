package com.example.myfinanceapplication.view.cost

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myfinanceapplication.R
import com.example.myfinanceapplication.databinding.FragmentAddIncomeBinding
import com.example.myfinanceapplication.view.BackgroundFragment
import com.example.myfinanceapplication.viewModel.AddCostViewModel
import com.example.myfinanceapplication.viewModel.CostViewModel

class AddIncomeFragment : Fragment() {
    lateinit var binding: FragmentAddIncomeBinding
    private lateinit var viewModel: CostViewModel
    private lateinit var addCostViewModel: AddCostViewModel
    var category = ""

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

    fun setupUI() {
        binding.apply {
            tvBtnCategory?.text = "Выберите категорию"

            ibSave.setOnClickListener {
                val title = etTitle.text.toString().trim()
                val sum = etSum.text.toString().replace(" ", "")
                //val category = spinnerCategory?.selectedItem.toString()
                val comment = etMultyLineComment.text.toString()
                if (addCostViewModel.checkDataIncome(
                        title,
                        sum,
                        category,
                        comment,
                        viewModel.getBalanceNow()
                    )
                ) {
                    (activity as IncomeActivity).closeFragments()
                } else {
                    // Обработка ошибок
                    Toast.makeText(
                        (activity as IncomeActivity),
                        "Заполните все необходимые поля",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            ibClose.setOnClickListener {
                (activity as IncomeActivity).closeFragments()
            }

            ibtnCategory.setOnClickListener {
                //parentFragment?.view?.visibility = View.INVISIBLE
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.backgroundFragment, BackgroundFragment())
                    .addToBackStack(null)
                    .commit()

                val categoriesFragment = CategoriesFragmentForIncome()
                categoriesFragment.setTargetFragment(this@AddIncomeFragment, 1)
                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .add(R.id.place_holder_addIncomeFragment, categoriesFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    fun receiveData(data: String) {
        category = data
        binding.apply {
            tvBtnCategory?.text = if (data != "")
                "Выбрана: $data"
            else
                "Выберите категорию"
        }
    }

}