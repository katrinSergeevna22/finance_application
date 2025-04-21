package com.example.myfinanceapplication.model.utils

import android.content.Context
import android.content.Intent
import com.example.myfinanceapplication.MainActivity
import com.example.myfinanceapplication.R
import com.example.myfinanceapplication.view.AuthActivity
import com.example.myfinanceapplication.view.cost.ExpensesActivity
import com.example.myfinanceapplication.view.cost.IncomeActivity
import com.example.myfinanceapplication.view.goals.GoalsActivity
import com.example.myfinanceapplication.view.tips.TipsActivity
import com.example.myfinanceapplication.viewModel.AuthViewModel

enum class NavigationTitle {
    GOALS, TIPS, INCOME, EXPENSE, TIPS_WITH_SELECT, GOALS_WITH_SELECT, MAIN, AUTH,
}

const val SELECTED_ITEM = "selectedItem"

fun getIntentForNavigation(
    context: Context,
    nameActivity: NavigationTitle,
    titleOfGoalsOrTips: String = "",
): Intent {
    var intent = Intent()
    when (nameActivity) {
        NavigationTitle.GOALS -> {
            intent = Intent(context, GoalsActivity::class.java)
        }

        NavigationTitle.GOALS_WITH_SELECT -> {
            intent = Intent(context, GoalsActivity::class.java).putExtra(
                SELECTED_ITEM,
                titleOfGoalsOrTips
            )
        }

        NavigationTitle.TIPS -> {
            intent = Intent(context, TipsActivity::class.java)
        }

        NavigationTitle.TIPS_WITH_SELECT -> {
            intent = Intent(context, TipsActivity::class.java).putExtra(
                SELECTED_ITEM,
                titleOfGoalsOrTips
            )
        }

        NavigationTitle.INCOME -> {
            intent = Intent(context, IncomeActivity::class.java)
        }

        NavigationTitle.EXPENSE -> {
            intent = Intent(context, ExpensesActivity::class.java)
        }

        NavigationTitle.AUTH -> {
            AuthViewModel().exit()
            intent = Intent(context, AuthActivity::class.java)
        }

        NavigationTitle.MAIN -> intent = Intent(context, MainActivity::class.java)
    }
    return intent
}

fun navigationForNavigationView(context: Context, itemId: Int): Intent {
    return when (itemId) {
        R.id.menu_home -> {
            getIntentForNavigation(context, NavigationTitle.MAIN)
        }

        R.id.menu_tips -> {
            getIntentForNavigation(context, NavigationTitle.TIPS)
        }

        R.id.menu_income -> {
            getIntentForNavigation(context, NavigationTitle.INCOME)
        }

        R.id.menu_expense -> {
            getIntentForNavigation(context, NavigationTitle.EXPENSE)
        }

        R.id.menu_goals -> {
            getIntentForNavigation(context, NavigationTitle.GOALS)
        }

        R.id.menu_exit -> {
            getIntentForNavigation(context, NavigationTitle.AUTH)
        }

        else -> {
            Intent()
        }
    }
}