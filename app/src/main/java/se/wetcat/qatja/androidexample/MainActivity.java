package se.wetcat.qatja.androidexample;

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

import android.os.Bundle;
import android.util.Log;

import se.wetcat.qatja.MQTTConnectionConstants;
import se.wetcat.qatja.androidexample.fragments.BrokerFragment;
import se.wetcat.qatja.androidexample.fragments.ChannelFragment;

import static se.wetcat.qatja.MQTTConnectionConstants.STATE_CONNECTED;
import static se.wetcat.qatja.MQTTConnectionConstants.STATE_NONE;

public class MainActivity extends ConnectionActivity
    implements BrokerFragment.OnBrokerFragmentChangedListener, ChannelFragment.OnChannelFragmentListener {

  private static final String TAG = MainActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  @Override
  protected void onResume() {
    super.onResume();

//    if (getSupportFragmentManager().findFragmentByTag(BrokerFragment.class.getSimpleName()) == null) {
//      getSupportFragmentManager()
//          .beginTransaction()
//          .replace(R.id.container, BrokerFragment.newInstance(), BrokerFragment.class.getSimpleName())
//          .commit();
//    }
  }

  @Override
  public void onConnect(String host, String identifier) {
    connect(identifier, host);
  }

  @Override
  public void onStateChanged(int newState) {
    Log.d(TAG, "onStateChanged(" + MQTTConnectionConstants.resolveStateName(newState) + ")");


    if (newState == STATE_NONE) {
      if (getSupportFragmentManager().findFragmentByTag(BrokerFragment.class.getSimpleName()) == null) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.container, BrokerFragment.newInstance(), BrokerFragment.class.getSimpleName())
            .commit();
      }
    } else if (newState == STATE_CONNECTED) {
      if (getSupportFragmentManager().findFragmentByTag(ChannelFragment.class.getSimpleName()) == null) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.container, ChannelFragment.newInstance(), ChannelFragment.class.getSimpleName())
            .commit();
      }
    }
  }

  @Override
  public void onJoin(String channelName) {
    Log.e("ANTE", "onJoin(" + channelName + ")");

    subscribe(channelName);
  }
}
