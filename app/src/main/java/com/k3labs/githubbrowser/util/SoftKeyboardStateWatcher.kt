package com.k3labs.githubbrowser.util

import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import java.util.*

/**
 * Notifies on keyboard events show / hide
    implement as:
            softKeyboardStateWatcher = SoftKeyboardStateWatcher(binding.root).apply {
                addSoftKeyboardStateListener(object :
                    SoftKeyboardStateWatcher.SoftKeyboardStateListener {
                    override fun onSoftKeyboardOpened(keyboardHeightInPx: Int) {
//                        binding.bottomNavigationView.visibility = View.GONE
                    }

                    override fun onSoftKeyboardClosed() {
//                        binding.bottomNavigationView.visibility = View.VISIBLE
                    }
                })
            }
 */
class SoftKeyboardStateWatcher @JvmOverloads constructor(
    private val activityRootView: View,
    private var isSoftKeyboardOpened: Boolean = false
) : OnGlobalLayoutListener {

    private val listeners: MutableList<SoftKeyboardStateListener> = LinkedList()

    /**
     * Default value is zero `0`.
     *
     * @return last saved keyboard height in px
     */
    private var lastSoftKeyboardHeightInPx = 0

    override fun onGlobalLayout() {
        val r = Rect()
        //r will be populated with the coordinates of your view that area still visible.
        activityRootView.getWindowVisibleDisplayFrame(r)
        val heightDiff = activityRootView.rootView.height - (r.bottom - r.top)
        if (!isSoftKeyboardOpened && heightDiff > activityRootView.rootView
                .height / 4
        ) { // if more than 100 pixels, its probably a keyboard...
            isSoftKeyboardOpened = true
            notifyOnSoftKeyboardOpened(heightDiff)
        } else if (isSoftKeyboardOpened && heightDiff < activityRootView.rootView
                .height / 4
        ) {
            isSoftKeyboardOpened = false
            notifyOnSoftKeyboardClosed()
        }
    }

    fun addSoftKeyboardStateListener(listener: SoftKeyboardStateListener) {
        listeners.add(listener)
    }

    fun removeSoftKeyboardStateListener(listener: SoftKeyboardStateListener) {
        listeners.remove(listener)
    }

    private fun notifyOnSoftKeyboardOpened(keyboardHeightInPx: Int) {
        lastSoftKeyboardHeightInPx = keyboardHeightInPx
        for (listener in listeners) {
            listener.onSoftKeyboardOpened(keyboardHeightInPx)
        }
    }

    private fun notifyOnSoftKeyboardClosed() {
        for (listener in listeners) {
            listener.onSoftKeyboardClosed()
        }
    }

    init {
        activityRootView.viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    interface SoftKeyboardStateListener {
        fun onSoftKeyboardOpened(keyboardHeightInPx: Int)
        fun onSoftKeyboardClosed()
    }
}