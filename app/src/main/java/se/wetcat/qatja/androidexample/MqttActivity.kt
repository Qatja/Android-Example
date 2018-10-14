package se.wetcat.qatja.androidexample

/*
 * Copyright (C) 2017 Andreas Goransson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import se.wetcat.qatja.MQTTConstants
import se.wetcat.qatja.android.MQTTConnectionConstants
import se.wetcat.qatja.android.QatjaService
import se.wetcat.qatja.messages.MQTTPublish

abstract class MqttActivity : ConnectivityActivity() {

    companion object {
        const val TAG = "MqttActivity"
    }

    lateinit var mClient: QatjaService

    private var isBound = false
    private var isBinding = false

    private val mHandler: Handler = Handler(MqttCallback())

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
