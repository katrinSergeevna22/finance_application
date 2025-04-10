package com.example.myfinanceapplication.view.cost

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
            fetchNewCategories(categoriesArray)
            selectedCategory()
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
                llNewCategory1.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory2.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory3.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory4.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)

                selectingCategory = categoriesArray[2]
            }
            llTransport.setOnClickListener {
                selectingCategory = categoriesArray[3]
                etNewCategory.visibility = View.INVISIBLE
                llHome.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTransport.background =
                    resources.getDrawable(R.drawable.shape_rectangle_contur_violet)
                llFood.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llHobby.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTecnic.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llEducation.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llShopping.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llOther.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory1.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory2.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory3.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory4.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
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
                llNewCategory1.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory2.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory3.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory4.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
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
                llNewCategory1.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory2.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory3.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory4.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
            }
            llTecnic.setOnClickListener {
                selectingCategory = categoriesArray[6]
                etNewCategory.visibility = View.INVISIBLE
                llHome.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTransport.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llFood.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llHobby.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTecnic.background =
                    resources.getDrawable(R.drawable.shape_rectangle_contur_violet)
                llEducation.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llShopping.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llOther.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory1.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory2.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory3.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory4.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
            }
            llEducation.setOnClickListener {
                selectingCategory = categoriesArray[7]
                etNewCategory.visibility = View.INVISIBLE
                llHome.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTransport.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llFood.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llHobby.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTecnic.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llEducation.background =
                    resources.getDrawable(R.drawable.shape_rectangle_contur_violet)
                llShopping.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llOther.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory1.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory2.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory3.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory4.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
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
                llShopping.background =
                    resources.getDrawable(R.drawable.shape_rectangle_contur_violet)
                llOther.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory1.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory2.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory3.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory4.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
            }
            llOther.setOnClickListener {
                selectingCategory = categoriesArray[1]
                etNewCategory.visibility = View.VISIBLE
                etNewCategory.setText(categoriesArray[1])
                llHome.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTransport.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llFood.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llHobby.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTecnic.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llEducation.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llShopping.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llOther.background = resources.getDrawable(R.drawable.shape_rectangle_contur_violet)
                llNewCategory1.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory2.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory3.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory4.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
            }
            llNewCategory1.setOnClickListener {
                selectingCategory = tvNewCategory1.text.toString()
                etNewCategory.visibility = View.VISIBLE
                llHome.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTransport.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llFood.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llHobby.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTecnic.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llEducation.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llShopping.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llOther.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory1.background =
                    resources.getDrawable(R.drawable.shape_rectangle_contur_violet)
                llNewCategory2.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory3.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory4.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
            }
            llNewCategory2.setOnClickListener {
                selectingCategory = tvNewCategory2.text.toString()
                etNewCategory.visibility = View.VISIBLE
                llHome.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTransport.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llFood.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llHobby.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTecnic.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llEducation.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llShopping.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llOther.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory2.background =
                    resources.getDrawable(R.drawable.shape_rectangle_contur_violet)
                llNewCategory1.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory3.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory4.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
            }
            llNewCategory3.setOnClickListener {
                selectingCategory = tvNewCategory3.text.toString()
                etNewCategory.visibility = View.VISIBLE
                llHome.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTransport.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llFood.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llHobby.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTecnic.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llEducation.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llShopping.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llOther.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory3.background =
                    resources.getDrawable(R.drawable.shape_rectangle_contur_violet)
                llNewCategory2.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory1.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory4.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
            }
            llNewCategory4.setOnClickListener {
                selectingCategory = tvNewCategory4.text.toString()
                etNewCategory.visibility = View.VISIBLE
                llHome.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTransport.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llFood.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llHobby.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llTecnic.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llEducation.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llShopping.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llOther.background = resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory4.background =
                    resources.getDrawable(R.drawable.shape_rectangle_contur_violet)
                llNewCategory2.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory3.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
                llNewCategory1.background =
                    resources.getDrawable(R.drawable.shape_rectangle_all_white)
            }
            ibSave.setOnClickListener {
                Log.d("btnSave", selectingCategory)
                val newCategory = etNewCategory.text.toString()
//                viewModel.category.value =
//                    if (selectingCategory == categoriesArray[1])
//                        newCategory
//                    else selectingCategory
                (targetFragment as? AddExpenseFragment)?.receiveData(
                    if (selectingCategory == categoriesArray[1] && newCategory.isNotEmpty())
                        newCategory
                    else selectingCategory
                )
                (targetFragment as? EditExpenseFragment)?.receiveData(
                    if (selectingCategory == categoriesArray[1] && newCategory.isNotEmpty())
                        newCategory
                    else selectingCategory
                )
                (activity as ExpensesActivity).closeFragments()
                //requireActivity().supportFragmentManager.popBackStack()
            }
            ibSelectGoal.setOnClickListener {
                (targetFragment as? AddExpenseFragment)?.receiveData(
                    categoriesArray[0]
                )
                (targetFragment as? EditExpenseFragment)?.receiveData(
                    categoriesArray[0]
                )
                (activity as ExpensesActivity).closeFragments()
            }
        }

        return binding.root
    }

    private fun selectedCategory() {
        val selectingCategory = (targetFragment as? EditExpenseFragment)?.selectingCategory
        Log.d("katrin_selectedCategory", selectingCategory.toString())
        Log.d("katrin_selectedCategory_tv1", binding.tvCategoryText1.text.toString())
        binding.apply {
            when (selectingCategory) {
                tvCategoryText1.text -> llHome.background =
                    resources.getDrawable(R.drawable.shape_rectangle_contur_violet)

                tvCategoryText2.text -> llTransport.background =
                    resources.getDrawable(R.drawable.shape_rectangle_contur_violet)

                tvCategoryText3.text -> llFood.background =
                    resources.getDrawable(R.drawable.shape_rectangle_contur_violet)

                tvCategoryText4.text -> llHobby.background =
                    resources.getDrawable(R.drawable.shape_rectangle_contur_violet)

                tvCategoryText5.text -> llTecnic.background =
                    resources.getDrawable(R.drawable.shape_rectangle_contur_violet)

                tvCategoryText6.text -> llEducation.background =
                    resources.getDrawable(R.drawable.shape_rectangle_contur_violet)

                tvCategoryText7.text -> llShopping.background =
                    resources.getDrawable(R.drawable.shape_rectangle_contur_violet)

                tvCategoryText8.text -> llOther.background =
                    resources.getDrawable(R.drawable.shape_rectangle_contur_violet)

                tvNewCategory1.text -> llNewCategory1.background =
                    resources.getDrawable(R.drawable.shape_rectangle_contur_violet)

                tvNewCategory2.text -> llNewCategory2.background =
                    resources.getDrawable(R.drawable.shape_rectangle_contur_violet)

                tvNewCategory3.text -> llNewCategory3.background =
                    resources.getDrawable(R.drawable.shape_rectangle_contur_violet)

                tvNewCategory4.text -> llNewCategory4.background =
                    resources.getDrawable(R.drawable.shape_rectangle_contur_violet)
                "Цель" -> {
//                    ibSelectGoal.background =
//                        resources.getDrawable(R.drawable.shape_rectangle_contur_violet)
//                    tvSelectGoal.setTextColor(resources.getColor(R.color.dark_violet))
                }
                else -> {
                    etNewCategory.setText(selectingCategory)
                }
            }
        }
    }

    private fun fetchNewCategories(categoriesArray: Array<String>) {
        val newCategories = (activity as ExpensesActivity).categoriesList.toSet()
            .filter { !categoriesArray.contains(it) }
        if (newCategories.isNotEmpty()) {
            binding.apply {
                tvNewCategory1.text = newCategories[0]
                switchVisibleNewCategories(llNewCategory1, ivCategoryImage1, tvNewCategory1)
                if (newCategories.size > 1) {
                    tvNewCategory2.text = newCategories[1]
                    switchVisibleNewCategories(llNewCategory2, ivCategoryImage2, tvNewCategory2)
                }
                if (newCategories.size > 2) {
                    tvNewCategory3.text = newCategories[2]
                    switchVisibleNewCategories(llNewCategory3, ivCategoryImage3, tvNewCategory3)
                }
                if (newCategories.size > 3) {
                    tvNewCategory4.text = newCategories[3]
                    switchVisibleNewCategories(llNewCategory4, ivCategoryImage4, tvNewCategory4)
                }
            }
        }
    }

    private fun switchVisibleNewCategories(
        linearLayout: LinearLayout,
        imageView: ImageView,
        textView: TextView
    ) {
        binding.apply {
            linearLayout.visibility = View.VISIBLE
            imageView.visibility = View.VISIBLE
            textView.visibility = View.VISIBLE
        }
    }
}