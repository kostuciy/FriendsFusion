package com.kostuciy.friendsfusion.core.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager


object AppUtils {

    fun hideKeyboard(activity: Activity?) {
        val view: View? = activity?.currentFocus ?: return
        if (view != null) {
            val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}