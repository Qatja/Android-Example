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
import kotlinx.android.synthetic.main.fragment_broker.*
import se.wetcat.qatja.androidexample.R
import se.wetcat.qatja.androidexample.entry

interface OnBrokerFragmentChangedListener {
    fun onConnect(host: String, identifier: String)
}

class BrokerFragment : Fragment() {

    companion object {
        const val TAG = "BrokerFragment"

        const val ARG_HOST = "host"
        const val ARG_IDENTIFIER = "identifier"

        fun newInstance(host: String?, identifier: String?) = BrokerFragment().apply {
            arguments = Bundle().apply {
                if (!host.isNullOrBlank()) {
                    putString(ARG_HOST, host)
                }
                if (!identifier.isNullOrBlank()) {
                    putString(ARG_HOST, host)
                }
            }
        }
    }

    private var mHost: String? = null

    private var mIdentifier: String? = null

    private var mListener: OnBrokerFragmentChangedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getString(ARG_HOST)?.let {
            mHost = it
        }

        arguments?.getString(ARG_IDENTIFIER)?.let {
            mIdentifier = it
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_broker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!mHost.isNullOrBlank()) {
            hostUri.setText(mHost)
        }

        if (!mIdentifier.isNullOrBlank()) {
            clientIdentifier.setText(mIdentifier)
        }

        connect.setOnClickListener {
            mListener?.onConnect(hostUri.entry(), clientIdentifier.entry())
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context is OnBrokerFragmentChangedListener) {
            mListener = context
        } else {
            throw RuntimeException("${context.toString()} must implement OnBrokerFragmentChangedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()

        mListener = null
    }

}
