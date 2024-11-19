package es.jvbabi.vplanplus.ui.common

import android.content.Context
import android.content.Intent
import android.net.Uri

fun openLink(context: Context, url: String) {
    val browserIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse(url)
    )
    context.startActivity(browserIntent, null)
}