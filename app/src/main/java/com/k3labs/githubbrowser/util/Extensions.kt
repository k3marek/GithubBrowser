package com.k3labs.githubbrowser.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.k3labs.githubbrowser.R
import com.k3labs.githubbrowser.StartActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar


fun View.dismissKeyboard(activity: Activity?) {
    val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(windowToken, 0)
}

fun String.snackbar(activity: StartActivity, duration: Int = Snackbar.LENGTH_SHORT) {
    val coordinatorLayout = (activity.findViewById(R.id.main) as CoordinatorLayout)
    val snack = Snackbar.make(coordinatorLayout, this, duration)
    val params = snack.view.layoutParams as CoordinatorLayout.LayoutParams
    snack.view.layoutParams
    params.setMargins(
        params.leftMargin, params.topMargin, params.rightMargin,
        coordinatorLayout.findViewById<BottomNavigationView>(R.id.bottomNavigationView).height + 16
    )
    snack.view.layoutParams = params
    snack.show()
}

fun String.toast(context: Context, duration: Int = Toast.LENGTH_SHORT): Toast {
    return Toast.makeText(context, this, duration).apply { show() }
}