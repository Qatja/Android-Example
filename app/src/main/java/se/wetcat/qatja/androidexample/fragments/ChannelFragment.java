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

public class ChannelFragment extends Fragment {

  private EditText mChannel;

  private OnChannelFragmentListener mListener;

  public interface OnChannelFragmentListener {
    void onJoin(String channelName);
  }

  public ChannelFragment() {
    // Required empty public constructor
  }

  public static ChannelFragment newInstance() {
    ChannelFragment fragment = new ChannelFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_channel, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mChannel = (EditText) view.findViewById(R.id.channel);

    view.findViewById(R.id.join).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (mListener != null) {
          mListener.onJoin(mChannel.getText().toString());
        }
      }
    });
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

    if (context instanceof OnChannelFragmentListener) {
      mListener = (OnChannelFragmentListener) context;
    } else {
      throw new RuntimeException(context.toString() + " must implement OnChannelFragmentListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();

    mListener = null;
  }

}
