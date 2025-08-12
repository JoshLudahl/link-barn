package com.softklass.linkbarn.utils

import android.content.Context
import android.content.Intent

fun shareAppIntent(context: Context) {
    val packageName = context.packageName
    val shareText = "Check out LinkBarn: https://play.google.com/store/apps/details?id=$packageName"
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "LinkBarn")
        putExtra(Intent.EXTRA_TEXT, shareText)
    }
    val chooser = Intent.createChooser(sendIntent, "Share LinkBarn")
    context.startActivity(chooser, null)
}
