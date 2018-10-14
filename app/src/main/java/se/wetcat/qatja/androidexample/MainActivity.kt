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

import android.os.Bundle
import android.util.Log.d
import se.wetcat.qatja.androidexample.fragments.BrokerFragment
import se.wetcat.qatja.androidexample.fragments.ChannelFragment
import se.wetcat.qatja.androidexample.fragments.OnBrokerFragmentChangedListener
import se.wetcat.qatja.androidexample.fragments.OnChannelFragmentListener

class MainActivity : MqttActivity(), OnBrokerFragmentChangedListener, OnChannelFragmentListener {

    companion object {
        const val TAG = "MainActivity"
    }

    private var mEnteredHost: String? = null

    private var mEnteredIdentifier: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        if (supportFragmentManager.findFragmentByTag(BrokerFragment::class.java.simpleName) == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, BrokerFragment.newInstance(mEnteredHost, mEnteredIdentifier), BrokerFragment::class.java.simpleName)
                    .commit();
        }
    }

    override fun onConnect(host: String, identifier: String) {
        mEnteredHost = host
        mEnteredIdentifier = identifier

        attemptConnectMqtt(host, identifier)
    }

    override fun onJoin(channelName: String) {
        if (isDebug()) {
            d(TAG, "onJoin($channelName)")
        }

        attemptSubscribeToTopic(channelName)
    }

    override fun onMqttConnected() {
        if (supportFragmentManager.findFragmentByTag(ChannelFragment::class.java.simpleName) == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, ChannelFragment.newInstance(), ChannelFragment::class.java.simpleName)
                    .commit()
        }
    }

    override fun onMqttDisconnected() {
        if (supportFragmentManager.findFragmentByTag(BrokerFragment::class.java.simpleName) == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, BrokerFragment.newInstance(mEnteredHost, mEnteredIdentifier), BrokerFragment::class.java.simpleName)
                    .commit()
        }
    }

}
