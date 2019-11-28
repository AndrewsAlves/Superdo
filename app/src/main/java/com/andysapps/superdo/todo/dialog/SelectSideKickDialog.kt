package com.andysapps.superdo.todo.dialog


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.fragments.EditTaskFragment
import com.andysapps.superdo.todo.model.Task
import com.andysapps.superdo.todo.model.sidekicks.*
import kotlinx.android.synthetic.main.fragment_select_side_kick_dialog.*

/**
 * A simple [Fragment] subclass.
 */

class SelectSideKickDialog : DialogFragment(), View.OnClickListener {

    var task : Task = Task()

    companion object {
        fun instance(task : Task) : SelectSideKickDialog {
            val fragment = SelectSideKickDialog()
            fragment.task = task
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_side_kick_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initOnClicks()
        updateUi()


    }

    fun initOnClicks() {

        sk_btn_deadline.setOnClickListener(this)
        sk_btn_subtasks.setOnClickListener(this)
        sk_btn_remind.setOnClickListener(this)
        sk_btn_focus.setOnClickListener(this)
        sk_btn_contact_card.setOnClickListener(this)
        sk_btn_location.setOnClickListener(this)

        sk_b_possitive.setOnClickListener {

        }

        sk_b_negative.setOnClickListener {
            dismiss()
        }
    }

    fun updateUi() {

        iv_sk_deadline_sel.visibility = View.GONE
        iv_sk_subtasks_sel.visibility = View.GONE
        iv_sk_remind_sel.visibility = View.GONE
        iv_sk_focus_sel.visibility = View.GONE
        iv_sk_contact_card_sel.visibility = View.GONE
        iv_sk_location_sel.visibility = View.GONE

        if (task.deadline != null && task.deadline.isEnabled) {
            iv_sk_deadline_sel.visibility = View.VISIBLE
        }

        if (task.subtasks != null && task.subtasks.isEnabled) {
            iv_sk_subtasks_sel.visibility = View.VISIBLE
        }

        if (task.remind != null && task.remind.isEnabled) {
            iv_sk_remind_sel.visibility = View.VISIBLE
        }

        if (task.focus != null && task.focus.isEnabled) {
            iv_sk_focus_sel.visibility = View.VISIBLE
        }

        if (task.contactCard != null && task.contactCard.isEnabled) {
            iv_sk_contact_card_sel.visibility = View.VISIBLE
        }

        if (task.location != null && task.location.isEnabled) {
            iv_sk_location_sel.visibility = View.VISIBLE
        }

    }

    override fun onDestroy() {
        super.onDestroy()
    }
 
    override fun onClick(v: View?) {
        when(v!!.id) {
            sk_btn_deadline.id -> {
                if (task.deadline == null) {
                    task.deadline = Deadline()
                }
                task.deadline.isEnabled = !task.deadline.isEnabled
            }
            sk_btn_subtasks.id -> {
                if (task.subtasks == null) {
                    task.subtasks = Subtasks()
                }
                task.subtasks.isEnabled = !task.subtasks.isEnabled
            }
            sk_btn_remind.id -> {
                if (task.remind == null) {
                    task.remind = Remind()
                }
                task.remind.isEnabled = !task.remind.isEnabled
            }
            sk_btn_focus.id -> {
                if (task.focus == null) {
                    task.focus = Focus()
                }
                task.focus.isEnabled = !task.focus.isEnabled
            }
            sk_btn_contact_card.id -> {
                if (task.contactCard == null) {
                    task.contactCard = ContactCard()
                }
                task.contactCard.isEnabled = !task.contactCard.isEnabled
            }
            sk_btn_location.id -> {
                if (task.location == null) {
                    task.location = Location()
                }
                task.location.isEnabled = !task.location.isEnabled
            }
        }

        updateUi()
    }


}
