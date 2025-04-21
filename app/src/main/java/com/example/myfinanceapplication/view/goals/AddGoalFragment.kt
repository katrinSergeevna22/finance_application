package com.example.myfinanceapplication.view.goals

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.myfinanceapplication.databinding.FragmentAddGoalBinding
import com.example.myfinanceapplication.viewModel.AddGoalViewModel


class AddGoalFragment : Fragment() {
    lateinit var binding: FragmentAddGoalBinding
    private lateinit var viewModel: AddGoalViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddGoalBinding.inflate(inflater)
        viewModel = ViewModelProvider(this).get(AddGoalViewModel::class.java)

        setupUI()
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddGoalFragment()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUI(){
        view?.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Закрываем клавиатуру и фрагмент при нажатии вне области фрагмента
                hideKeyboardFrom((activity as GoalsActivity), requireView())
                parentFragmentManager.popBackStack()
            }
            true
        }

        binding.apply {
            ibClose.setOnClickListener {
                (activity as GoalsActivity).closeFragments()
                //parentFragmentManager.popBackStack()
            }
            ibSave.setOnClickListener {
                val title = etTitle.text.toString().trim()
                val sum = etSum.text.toString().replace(" ", "")
                val category = spinnerCategory?.selectedItem.toString()
                val comment = etMultyLineComment.text.toString().trim()

                if (viewModel.checkData(title, sum, category, comment)) {
                    (activity as GoalsActivity).closeFragments()
                    //parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(
                        (activity as GoalsActivity),
                        viewModel.textOfToast,
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        }
    }

    private fun hideKeyboardFrom(context: Context, view: View){
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }



}