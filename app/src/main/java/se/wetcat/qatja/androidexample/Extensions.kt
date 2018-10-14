package se.wetcat.qatja.androidexample

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.widget.EditText
import androidx.fragment.app.Fragment

fun Context.isDebug(): Boolean {
    return this.resources.getBoolean(R.bool.is_debug)
}

fun Activity.isDebug(): Boolean {
    return this.resources.getBoolean(R.bool.is_debug)
}

fun Fragment.isDebug(): Boolean {
    return this.resources.getBoolean(R.bool.is_debug)
}

fun Activity.hasInternet(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = cm.activeNetworkInfo

    return activeNetwork?.isConnectedOrConnecting == true
}

fun Intent.printExtras() {
    if (extras != null) {
        for (key in extras!!.keySet()) {
            val value = extras!!.get(key)
            Log.d(action, "[$key] ${value!!} (${value.javaClass.name})")
        }
    }
}

fun EditText.entry(): String = this.text.toString()
