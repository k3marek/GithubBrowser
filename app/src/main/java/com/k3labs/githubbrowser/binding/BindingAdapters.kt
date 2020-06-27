package com.k3labs.githubbrowser.binding

import androidx.databinding.BindingAdapter
import android.view.View

/**
 * Data Binding adapters specific to the app.
 */
object BindingAdapters {

    @JvmStatic
    @BindingAdapter("visibleIf")
    fun bindVisibleIf(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("invisibleIf")
    fun bindInvisibleIf(view: View, visible: Boolean) {
        view.visibility = if (visible) View.INVISIBLE else View.VISIBLE
    }
    @JvmStatic
    @BindingAdapter("goneIf")
    fun bindGoneIf(view: View, visible: Boolean) {
        view.visibility = if (visible) View.GONE else View.VISIBLE
    }

    @JvmStatic
    @BindingAdapter("enabledIf")
    fun enabledIf(view: View, enabled: Boolean) {
        view.isEnabled = enabled
    }

    @JvmStatic
    @BindingAdapter("disabledIf")
    fun disabledIf(view: View, disabled: Boolean) {
        view.isEnabled = !disabled
    }

    @JvmStatic
    @BindingAdapter("visibleIfTextIsNotEmpty")
    fun bindVisibleIfTextIsNotEmpty(view: View, text:String?) {
        view.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE
    }
}
