package com.k3labs.githubbrowser.util

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Hides soft keyboard from view. It might use different context - todo: test in dialogs
 */
fun View.hideKeyboard() {
    val imm: InputMethodManager =
        context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(this.windowToken, 0)
}