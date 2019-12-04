package com.andysapps.superdo.todo.dialog.sidekicks


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter

import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.Utils
import com.andysapps.superdo.todo.enums.RepeatType
import com.andysapps.superdo.todo.model.sidekicks.Repeat
import kotlinx.android.synthetic.main.fragment_dlg_deadline.*
import kotlinx.android.synthetic.main.fragment_dlg_repeat.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */

class RepeatDialog : Fragment(), AdapterView.OnItemSelectedListener{

    var repeat : Repeat = Repeat()
    val repeatType = arrayOf(RepeatType.Day.name, RepeatType.Week.name, RepeatType.Month.name)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dlg_repeat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniiUi()
        updateUi()
    }

    fun iniiUi() {
        val aa = ArrayAdapter(activity!!.baseContext, android.R.layout.simple_spinner_item, repeatType)
        dlg_repeat_spinner_dwm.onItemSelectedListener = this
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dlg_repeat_spinner_dwm.setAdapter(aa)
        dlg_repeat_spinner_dwm.setSelection(0)
    }

    fun updateUi() {

        if (repeat.repeatType == RepeatType.Week) {
            dlg_repeat_ll_weekdays.visibility = View.VISIBLE
        } else {
            dlg_repeat_ll_weekdays.visibility = View.GONE
        }

        if (repeat.startDate == null) {
            repeat.startDate = Utils.getSuperdateFromTimeStamp(Calendar.getInstance().time.time)
            repeat.startDate.hasDate = true;
        }

        dlg_repeat_iv_startdate.setImageResource(R.drawable.ic_do_date_on)
        dlg_repeat_tv_startdate.setText(repeat.startDate.superDateString)
        dlg_repeat_tv_startdate.setTextColor(resources.getColor(R.color.grey4))


    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        repeat.repeatType = RepeatType.valueOf(repeatType[position])
        updateUi()
    }

}
