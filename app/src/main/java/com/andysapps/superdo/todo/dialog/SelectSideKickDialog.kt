package com.andysapps.superdo.todo.dialog


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.andysapps.superdo.todo.R
import kotlinx.android.synthetic.main.fragment_select_side_kick_dialog.*

/**
 * A simple [Fragment] subclass.
 */
class SelectSideKickDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_side_kick_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sk_b_possitive.setOnClickListener {

        }

        sk_b_negative.setOnClickListener {
            dismiss()
        }
    }


}
