package se.wetcat.qatja.androidexample.fragments;

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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import se.wetcat.qatja.androidexample.R;

public class BrokerFragment extends Fragment {

  private EditText mHost, mIdentifier;

  private OnBrokerFragmentChangedListener mListener;

  public static BrokerFragment newInstance() {
    Bundle args = new Bundle();
    BrokerFragment fragment = new BrokerFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public interface OnBrokerFragmentChangedListener {
    void onConnect(String host, String identifier);
  }

  public BrokerFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_broker, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mHost = (EditText) view.findViewById(R.id.host);

    mIdentifier = (EditText) view.findViewById(R.id.identifier);

    view.findViewById(R.id.connect).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mListener != null) {
          mListener.onConnect(mHost.getText().toString(), mIdentifier.getText().toString());
        }
      }
    });
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    if (context instanceof OnBrokerFragmentChangedListener) {
      mListener = (OnBrokerFragmentChangedListener) context;
    } else {
      throw new RuntimeException(context.toString() + " must implement OnBrokerFragmentChangedListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();

    mListener = null;
  }

}
