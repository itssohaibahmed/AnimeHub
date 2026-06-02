package com.sohaib.animehub.core.common.extensions

import android.content.Context
import android.content.Intent
import com.sohaib.animehub.core.common.R

fun Context?.shareText(text: String? = null) {
    this ?: return
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    val intent = Intent.createChooser(shareIntent, getString(R.string.settings_share_app))
    startActivity(intent)
}