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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import se.wetcat.qatja.android.QatjaService;
import se.wetcat.qatja.messages.MQTTPublish;

import static se.wetcat.qatja.MQTTConnectionConstants.STATE_CHANGE;
import static se.wetcat.qatja.MQTTConnectionConstants.STATE_CONNECTED;
import static se.wetcat.qatja.MQTTConnectionConstants.STATE_CONNECTING;
import static se.wetcat.qatja.MQTTConnectionConstants.STATE_CONNECTION_FAILED;
import static se.wetcat.qatja.MQTTConnectionConstants.STATE_NONE;
import static se.wetcat.qatja.MQTTConstants.EXACTLY_ONCE;
import static se.wetcat.qatja.MQTTConstants.PUBLISH;
import static se.wetcat.qatja.MQTTConstants.SUBACK;

/**
 * @author andreasgoransson0@gmail.com
 */
public abstract class ConnectionActivity extends AppCompatActivity
    implements QatjaService.StateListener {

  private static final String TAG = ConnectionActivity.class.getSimpleName();

  /**
   * The MQTT service.
   */
  private QatjaService mService;

  /**
   * Indicates if the MQTT connection is live
   */
  private boolean mBound = false;

  /**
   * This handler will recieve all callbacks from the MQTT service on the UI thread.
   */
  private Handler mHandler = new Handler(new MQTTCallback());

  @Override
  protected void onStart() {
    super.onStart();

    Intent service = new Intent(ConnectionActivity.this, QatjaService.class);
    bindService(service, connection, Context.BIND_AUTO_CREATE);
  }

  @Override
  protected void onStop() {
    super.onStop();

    unbindService(connection);
  }

  private class MQTTCallback implements Handler.Callback {
    @Override
    public boolean handleMessage(Message msg) {
      switch (msg.what) {
        case STATE_CHANGE:
          switch (msg.arg1) {
            case STATE_NONE:
              Toast.makeText(ConnectionActivity.this, "Not connected", Toast.LENGTH_SHORT).show();
              return true;
            case STATE_CONNECTING:
              Toast.makeText(ConnectionActivity.this, "Trying to connect...", Toast.LENGTH_SHORT).show();
              return true;
            case STATE_CONNECTED:
              Toast.makeText(ConnectionActivity.this, "Yay! Connected!", Toast.LENGTH_SHORT).show();
              return true;
            case STATE_CONNECTION_FAILED:
              Toast.makeText(ConnectionActivity.this, "Connection failed", Toast.LENGTH_SHORT).show();
              return true;
          }
          return true;

        case PUBLISH:
          MQTTPublish publish = (MQTTPublish) msg.obj;
          String topic = publish.getTopicName();
          byte[] payload = publish.getPayload();

          Toast.makeText(ConnectionActivity.this, new String(payload), Toast.LENGTH_SHORT).show();
          return true;

        case SUBACK:
          Toast.makeText(ConnectionActivity.this, "Subscribed", Toast.LENGTH_SHORT).show();
          return true;

        default:
          return false;
      }
    }
  }

  private ServiceConnection connection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
      Log.e(TAG, "onServiceConnected(" + name.getClassName() + ")");

      mService = ((QatjaService.QatjaBinder) binder).getService();
      mBound = true;

      mService.setStateListener(ConnectionActivity.this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      Log.e(TAG, "onServiceDisconnected(" + name.getClassName() + ")");

      mBound = false;
    }
  };

  /**
   * Attempt to perform the connection through the bound service
   *
   * @param identifier
   * @param host
   */
  protected void connect(
      String identifier,
      String host
  ) {
    Log.d(TAG, "connect(" + host + ")");

    if (!mBound || mService == null) {
      // Avoid attempting the connection if the service isn't fully bound and operational.
      return;
    }

    mService.setIdentifier(identifier);
    mService.setHandler(mHandler);
    mService.setHost(host);
    mService.setCleanSession(true);
    mService.connect();
  }

  protected void subscribe(String channel) {

    Log.d(TAG, "subscribe(" + channel + ")");

    if (!mBound || mService == null) {
      // Avoid attempting the connection if the service isn't fully bound and operational.
      return;
    }

    mService.subscribe(channel, EXACTLY_ONCE);
  }
}
