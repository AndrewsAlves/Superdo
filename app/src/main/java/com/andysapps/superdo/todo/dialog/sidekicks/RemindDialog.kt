package com.andysapps.superdo.todo.dialog.sidekicks


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.dialog.sidekicks.childfragment.RepeatFragment
import com.andysapps.superdo.todo.dialog.sidekicks.childfragment.RepeatOnceFragment
import com.andysapps.superdo.todo.enums.RemindType
import com.andysapps.superdo.todo.fragments.EditTaskFragment
import com.andysapps.superdo.todo.model.sidekicks.Remind
import kotlinx.android.synthetic.main.fragment_dlg_remind.*

/**
 * A simple [Fragment] subclass.
 */

class RemindDialog : DialogFragment(){

    var remind : Remind = Remind()

    companion object {
        fun instance(remind : Remind) : RemindDialog {
            val fragment = RemindDialog()
            fragment.remind = remind.clone()
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dlg_remind, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        updateUi()
    }

    fun initView() {

        if (remind.remindType == null) {
            remind.remindType = RemindType.REMIND_ONCE
        }

        Log.e("tag Ui updated", " UI updaed ")

        /////////
        // CLICKS

        dlg_remind_btn_remind_once.setOnClickListener {
            remind.remindType = RemindType.REMIND_ONCE
            updateUi()
        }

        dlg_remind_btn_repeat_remind.setOnClickListener {
            remind.remindType = RemindType.REMIND_REPEAT
            updateUi()
        }

    }

    fun updateUi() {

        dlg_remind_btn_remind_once.setImageResource(R.drawable.ic_dlg_remind_off)
        dlg_remind_btn_repeat_remind.setImageResource(R.drawable.ic_dlg_repeat_remind_off)

        when (remind.remindType) {

            RemindType.REMIND_ONCE -> {
                dlg_remind_btn_remind_once.setImageResource(R.drawable.ic_dlg_remind_on)
                setFragment(RepeatOnceFragment.instance(remind))
                Log.e("tag Ui updated", " UI updaed 2 ")
            }

            RemindType.REMIND_REPEAT -> {
                dlg_remind_btn_repeat_remind.setImageResource(R.drawable.ic_dlg_repeat_remind_on)
                setFragment(RepeatFragment.instance(remind.remindRepeat))
            }
        }
    }

    fun setFragment(fragment : Fragment) {
        val ft: FragmentTransaction = childFragmentManager.beginTransaction()
        ft.replace(R.id.dlg_framelayout__remind, fragment)
        ft.commit() // save the changes
        Log.e("tag Ui updated", " UI updaed fragment")
    }

}
