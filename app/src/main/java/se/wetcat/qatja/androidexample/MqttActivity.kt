package se.wetcat.qatja.androidexample

import android.accounts.AccountManager
import android.content.*
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import se.wetcat.qatja.MQTTConstants
import se.wetcat.qatja.android.MQTTConnectionConstants
import se.wetcat.qatja.android.QatjaService
import se.wetcat.qatja.messages.MQTTPublish

abstract class MqttActivity : ConnectivityActivity() {

    companion object {
        const val TAG = "MqttActivity"

        const val MQTT_PUBLISH = "se.wetcat.qatja.androidexample.action.MQTT_PUBLISH"
        const val MQTT_TOPIC = "se.wetcat.qatja.androidexample.data.MQTT_TOPIC"
        const val MQTT_PAYLOAD = "se.wetcat.qatja.androidexample.data.MQTT_PAYLOAD"
    }

    lateinit var mClient: QatjaService

    private var isBound = false
    private var isBinding = false

    private val mHandler: Handler = Handler(MqttCallback())

    private val mAccountManager: AccountManager by lazy {
        AccountManager.get(this)
    }

    private inner class MqttCallback : Handler.Callback {
        override fun handleMessage(msg: Message?): Boolean {
            msg?.let {
                when (it.what) {
                    MQTTConnectionConstants.STATE_CHANGE -> {
                        handleStateChange(it)
                    }
                    3 -> { //MQTTConstants.PUBLISH (has value 3!) MQTTPublish publish = (MQTTPublish) msg.obj;
                        handleReceivedMessage(it)
                    }
                }
            }

            return true
        }
    }

    private fun handleStateChange(msg: Message) {
        Log.e(TAG, MQTTConnectionConstants.resolveStateName(msg.arg1))

        when (msg.arg1) {
            MQTTConnectionConstants.STATE_NONE -> {
                onMqttDisconnected()
            }
            MQTTConnectionConstants.STATE_CONNECTING -> {
            }
            MQTTConnectionConstants.STATE_CONNECTED -> {
                onMqttConnected()
            }
            MQTTConnectionConstants.STATE_CONNECTION_FAILED -> {
                onMqttDisconnected()
            }
            else -> {
                Log.e(TAG, "Unhandled MQTT state change")
            }
        }
    }

    private fun handleReceivedMessage(msg: Message) {
        val publish: MQTTPublish = msg.obj as MQTTPublish

        if (isDebug()) {
            Log.d(TAG, "handleReceivedMessage(${publish.topicName})")
        }
    }

    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            mClient = (binder as QatjaService.QatjaBinder).service as QatjaService

            isBound = true
            isBinding = false

            mClient.setHandler(mHandler)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false
            isBinding = false
        }
    }

    override fun onResume() {
        super.onResume()

        attemptBindService()

        LocalBroadcastManager.getInstance(this).registerReceiver(mMqttReceiver, IntentFilter().apply {
            addAction(MQTT_PUBLISH)
        })
    }

    override fun onPause() {
        try {
            mClient.disconnect()

            unbindService(mConnection)

            isBinding = false
            isBound = false
        } catch (ex: IllegalArgumentException) {
            Log.e(TAG, "Couldn't unbind the service, this is probably fine considering the state changes in the app... can probably ignore this.", ex)
        }

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMqttReceiver)

        super.onPause()
    }

    protected fun attemptSubscribeToTopic(topic: String) {
        Log.d(TAG, "attemptSubscribeToTopic($topic)")

        launch(UI) {
            try {
                mClient.subscribe("", MQTTConstants.AT_MOST_ONCE)
            } catch (ex: Exception) {
                Log.e(TAG, ex.message, ex)
            }
        }
    }

    override fun onLoosingInternet() {
        mClient.disconnect()
    }

    override fun onGainedInternet() {
        if (isBound) {
//            attemptConnectMqtt()
        } else if (!isBound && !isBinding) {
            attemptBindService()
        }
    }

    private fun attemptBindService() {
        if (isDebug()) {
            Log.d(TAG, "attemptBindService()  [isBound: $isBound, isBinding: $isBinding]")
        }
        if (!isBound && !isBinding) {
            isBinding = true

            Intent(this@MqttActivity, QatjaService::class.java).apply {
                bindService(this, mConnection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    private val mMqttReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action == MQTT_PUBLISH) {
                    val topic = it.getStringExtra(MQTT_TOPIC)
                    val payload = it.getByteArrayExtra(MQTT_PAYLOAD)

                    mClient.publish(topic, payload)
                }
            }
        }
    }

    private fun handleEvent(intent: Intent) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    protected fun attemptConnectMqtt(host: String, identifier: String) {
        Log.d(TAG, "attemptConnectMqtt($host, $identifier)")

        if (!arrayOf(MQTTConnectionConstants.STATE_CONNECTING, MQTTConnectionConstants.STATE_CONNECTED).contains(mClient.state)) {
            try {
                mClient.setHost(host)
                mClient.setPort(1883)
                mClient.setIdentifier(identifier)
                mClient.setKeepAlive(5000)
                mClient.setCleanSession(true)

                mClient.connect()
            } catch (ex: Exception) {
                Log.e(TAG, ex.message, ex)
            }
        }
    }

    abstract fun onMqttConnected()

    abstract fun onMqttDisconnected()
}
