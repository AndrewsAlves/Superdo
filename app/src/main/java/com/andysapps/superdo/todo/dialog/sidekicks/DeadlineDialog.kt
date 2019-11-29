package com.andysapps.superdo.todo.dialog.sidekicks


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.andysapps.superdo.todo.R

/**
 * A simple [Fragment] subclass.
 */
class DeadlineDialog : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_deadline_dialog, container, false)
    }


}
