package com.example.myfinanceapplication.view.goals

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myfinanceapplication.databinding.FragmentAddGoalBinding
import com.example.myfinanceapplication.viewModel.AddGoalViewModel


class AddGoalFragment : Fragment() {
    lateinit var binding: FragmentAddGoalBinding
    private lateinit var viewModel: AddGoalViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
    private fun setupUI() {
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
            }
            ibSave.setOnClickListener {
                val title = etTitle.text.toString().trim()
                val sum = etSum.text.toString().replace(" ", "")
                val category = spinnerCategory?.selectedItem.toString()
                val comment = etMultyLineComment.text.toString().trim()

                if (viewModel.checkData(title, formattedSum(sum), category, comment)) {
                    etTitle.text.clear()
                    etSum.text.clear()
                    etMultyLineComment.text.clear()
                    (activity as GoalsActivity).closeFragments()
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

    private fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun formattedSum(sum: String): String {
        val cleanString = sum
            .replace(",", ".")
            .replace(Regex("[^\\d.]"), "")

        val formattedValue = when {
            cleanString.contains(".") -> {
                val parts = cleanString.split(".")
                when {
                    parts.size > 2 -> parts[0] + "." + parts[1].take(2)
                    parts[1].length > 2 -> parts[0] + "." + parts[1].take(2)
                    else -> cleanString
                }
            }

            else -> cleanString
        }

        return formattedValue
    }
}