package com.example.myfinanceapplication.view.cost

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myfinanceapplication.R
import com.example.myfinanceapplication.databinding.FragmentCategoriesBinding
import com.example.myfinanceapplication.view_model.AddCostViewModel

class CategoriesFragment : Fragment() {

    lateinit var binding: FragmentCategoriesBinding
    lateinit var viewModel: AddCostViewModel

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoriesBinding.inflate(inflater)
        viewModel = ViewModelProvider(requireActivity())[AddCostViewModel::class.java]

        binding.apply {
            val categoriesArray = resources.getStringArray(R.array.categoriesExpense)
            var selectingCategory = categoriesArray[1]
            llHome.setOnClickListener {
                etNewCategory.visibility = View.INVISIBLE
                llHome.background = resources.getDrawable(R.drawable.shape_rectangle_contur_violet)
                llTransport.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llFood.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llHobby.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTecnic.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llEducation.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llShopping.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llOther.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)

                selectingCategory = categoriesArray[2]
            }
            llTransport.setOnClickListener {
                selectingCategory = categoriesArray[3]
                etNewCategory.visibility = View.INVISIBLE
                llHome.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTransport.background = resources.getDrawable(R.drawable.shape_rectangle_contur_violet)
                llFood.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llHobby.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTecnic.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llEducation.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llShopping.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llOther.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
            }
            llFood.setOnClickListener {
                selectingCategory = categoriesArray[4]
                etNewCategory.visibility = View.INVISIBLE
                llHome.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTransport.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llFood.background = resources.getDrawable(R.drawable.shape_rectangle_contur_violet)
                llHobby.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTecnic.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llEducation.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llShopping.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llOther.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
            }
            llHobby.setOnClickListener {
                selectingCategory = categoriesArray[5]
                etNewCategory.visibility = View.INVISIBLE
                llHome.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTransport.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llFood.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llHobby.background = resources.getDrawable(R.drawable.shape_rectangle_contur_violet)
                llTecnic.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llEducation.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llShopping.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llOther.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
            }
            llTecnic.setOnClickListener {
                selectingCategory = categoriesArray[6]
                etNewCategory.visibility = View.INVISIBLE
                llHome.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTransport.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llFood.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llHobby.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTecnic.background = resources.getDrawable(R.drawable.shape_rectangle_contur_violet)
                llEducation.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llShopping.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llOther.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
            }
            llEducation.setOnClickListener {
                selectingCategory = categoriesArray[7]
                etNewCategory.visibility = View.INVISIBLE
                llHome.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTransport.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llFood.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llHobby.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTecnic.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llEducation.background = resources.getDrawable(R.drawable.shape_rectangle_contur_violet)
                llShopping.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llOther.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
            }
            llShopping.setOnClickListener {
                selectingCategory = categoriesArray[8]
                etNewCategory.visibility = View.INVISIBLE
                llHome.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTransport.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llFood.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llHobby.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTecnic.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llEducation.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llShopping.background = resources.getDrawable(R.drawable.shape_rectangle_contur_violet)
                llOther.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
            }
            llOther.setOnClickListener {
                selectingCategory = categoriesArray[1]
                etNewCategory.visibility = View.VISIBLE
                llHome.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTransport.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llFood.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llHobby.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTecnic.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llEducation.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llShopping.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llOther.background = resources.getDrawable(R.drawable.shape_rectangle_contur_violet)
            }
            ibSave.setOnClickListener {
                Log.d("btnSave", selectingCategory)
                val newCategory = etNewCategory.text.toString()
//                viewModel.category.value =
//                    if (selectingCategory == categoriesArray[1])
//                        newCategory
//                    else selectingCategory
                (targetFragment as? AddExpenseFragment)?.receiveData(
                    if (selectingCategory == categoriesArray[1])
                        newCategory
                    else selectingCategory
                )
                //(activity as ExpensesActivity).closeFragments()
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

        return binding.root
    }
}