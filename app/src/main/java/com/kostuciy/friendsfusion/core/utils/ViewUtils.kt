package com.kostuciy.friendsfusion.core.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.kostuciy.friendsfusion.R

object ViewUtils {
    fun ImageView.loadFromUrl(url: String) {
        Glide
            .with(this)
            .load(url)
            .placeholder(R.drawable.baseline_person_24)
            .into(this)
    }

    fun ImageView.loadFromUrlCircle(url: String) {
        Glide
            .with(this)
            .load(url)
            .circleCrop()
            .placeholder(R.drawable.baseline_person_24)
            .into(this)
    }
}
