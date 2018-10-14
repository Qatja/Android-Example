package se.wetcat.qatja.androidexample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity

interface InternetListener {
    fun onLoosingInternet()
    fun onGainedInternet()
}

abstract class ConnectivityActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ConnectivityActivity"
    }

    private val mConnectivityManager: ConnectivityManager by lazy {
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private var mCurrentInternetState = false
        private set(value) {
            if (value != field) {
                field = value
                if (field) {
                    onGainedInternet()
                } else {
                    onLoosingInternet()
                }
            }
        }

    private val mConnectivityReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            if (isDebug()) {
                intent?.printExtras()
            }

            if (intent?.action == ConnectivityManager.CONNECTIVITY_ACTION) {
                mCurrentInternetState = hasInternet()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        registerReceiver(mConnectivityReceiver, IntentFilter().apply {
            addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        })
    }

    override fun onPause() {
        super.onPause()

        unregisterReceiver(mConnectivityReceiver)
    }

    abstract fun onGainedInternet()

    abstract fun onLoosingInternet()

}
