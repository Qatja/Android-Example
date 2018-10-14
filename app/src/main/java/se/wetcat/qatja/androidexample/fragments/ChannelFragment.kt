package se.wetcat.qatja.androidexample.fragments

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

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_channel.*
import se.wetcat.qatja.androidexample.R
import se.wetcat.qatja.androidexample.entry

interface OnChannelFragmentListener {
    fun onJoin(channelName: String)
}

class ChannelFragment : Fragment() {

    companion object {
        const val TAG = "ChannelFragment"

        fun newInstance() = ChannelFragment().apply {
        }
    }

    private var mListener: OnChannelFragmentListener? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_channel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        join.setOnClickListener {
            mListener?.onJoin(channel.entry())
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is OnChannelFragmentListener) {
            mListener = context
        } else {
            throw RuntimeException("${context.toString()} must implement OnChannelFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()

        mListener = null
    }

}
