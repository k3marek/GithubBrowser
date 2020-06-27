package com.k3labs.githubbrowser.binding

import android.graphics.drawable.Drawable
import android.widget.ImageButton
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.k3labs.githubbrowser.R
import javax.inject.Inject

/**
 * Binding adapters that work with a fragment instance.
 */
class FragmentBindingAdapters @Inject constructor(val fragment: Fragment) {

    @BindingAdapter(
        value = ["imageUrl", "imageRequestListener", "placeholderDrawable", "errorDrawable"],
        requireAll = false
    )
    fun bindImage(
        imageView: ImageView,
        url: String?,
        listener: RequestListener<Drawable?>?,
        placeholderDrawable: Drawable?,
        errorDrawable: Drawable?
    ) {
        url?.let {
            val requestOptions = RequestOptions()
            placeholderDrawable?.let { requestOptions.placeholder(it) }
            errorDrawable?.let { requestOptions.error(it) }
            Glide.with(fragment)
                .load(it)
                .listener(listener)
                .apply(requestOptions)
                .into(imageView)
        }
    }


    @BindingAdapter(value = ["favToggleImage"])
    fun favToggleImage(imageButton: ImageButton, isFav: Boolean) {
        imageButton.setImageResource(if (isFav) R.drawable.ic_bookmark_black_24dp else R.drawable.ic_bookmark_border_black_24dp)
    }
}

