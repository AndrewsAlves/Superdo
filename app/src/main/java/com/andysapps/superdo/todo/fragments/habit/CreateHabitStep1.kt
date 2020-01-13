package com.andysapps.superdo.todo.fragments.habit


import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.Utils
import com.andysapps.superdo.todo.adapters.HabitSuggestionRecyclerAdapter
import com.andysapps.superdo.todo.enums.HabitCategory
import com.andysapps.superdo.todo.events.habit.SelectedHabitSuggestionEvent
import com.andysapps.superdo.todo.model.Habit
import kotlinx.android.synthetic.main.fragment_create_habit_step1.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * A simple [Fragment] subclass.
 */

class CreateHabitStep1 : Fragment() , TextWatcher {

    var habit = Habit()
    var isEditing : Boolean = false

    var suggestionList = ArrayList<String>()
    var adapter : HabitSuggestionRecyclerAdapter? = null

    companion object {
        fun instance(habit : Habit) : Fragment {
            val fragment = CreateHabitStep1()
            fragment.habit = habit
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_habit_step1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initClicks()
        updateUi()
        EventBus.getDefault().register(this)
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    fun initUi() {

        adapter = HabitSuggestionRecyclerAdapter(context, suggestionList)
        ch_rv_suggestions.layoutManager =  LinearLayoutManager(activity)
        ch_rv_suggestions.adapter = adapter

        if (!isEditing) {
            habit.habitCategory = HabitCategory.Health
            adapter!!.updateList(Utils.getHealthSuggestionList())
            habit.isCreatehabit = true
        }

        ch_et_habit_name.imeOptions = EditorInfo.IME_ACTION_DONE
        ch_et_habit_name.setRawInputType(InputType.TYPE_CLASS_TEXT)
        ch_et_habit_name.addTextChangedListener(this)

    }

    fun initClicks() {
        ch_btn_health.setOnClickListener {
            habit.habitCategory = HabitCategory.Health
            updateUi()
        }
        ch_btn_finance.setOnClickListener {
            habit.habitCategory = HabitCategory.finance
            updateUi()
        }
        ch_btn_productivity.setOnClickListener {
            habit.habitCategory = HabitCategory.Productivity
            updateUi()
        }
        ch_btn_digital_wellbeing.setOnClickListener {
            habit.habitCategory = HabitCategory.DigitalWellbeign
            updateUi()
        }
        ch_btn_mindfullness.setOnClickListener {
            habit.habitCategory = HabitCategory.Mindfullness
            updateUi()
        }
        ch_btn_addiction.setOnClickListener {
            habit.habitCategory = HabitCategory.Addiction
            updateUi()
        }
    }

    fun updateUi() {

        ch_btn_health.setBackgroundResource(R.drawable.bg_capsul_stroke_grey1)
        iv_ch_health.setImageResource(R.drawable.ic_ch_health_off)
        tv_ch_heath.setTextColor(resources.getColor(R.color.grey1))

        ch_btn_finance.setBackgroundResource(R.drawable.bg_capsul_stroke_grey1)
        iv_ch_finance.setImageResource(R.drawable.ic_ch_finance_off)
        tv_ch_finance.setTextColor(resources.getColor(R.color.grey1))

        ch_btn_productivity.setBackgroundResource(R.drawable.bg_capsul_stroke_grey1)
        iv_ch_productivity.setImageResource(R.drawable.ic_ch_productivity_off)
        tv_ch_productivity.setTextColor(resources.getColor(R.color.grey1))

        ch_btn_digital_wellbeing.setBackgroundResource(R.drawable.bg_capsul_stroke_grey1)
        iv_ch_digital_wellbeing.setImageResource(R.drawable.ic_ch_digital_wellbeing_off)
        tv_ch_digital_wellbeing.setTextColor(resources.getColor(R.color.grey1))

        ch_btn_mindfullness.setBackgroundResource(R.drawable.bg_capsul_stroke_grey1)
        iv_ch_mindfullness.setImageResource(R.drawable.ic_ch_mindfullness_off)
        tv_ch_mindfullness.setTextColor(resources.getColor(R.color.grey1))

        ch_btn_addiction.setBackgroundResource(R.drawable.bg_capsul_stroke_grey1)
        iv_ch_addiction.setImageResource(R.drawable.ic_ch_addiction_off)
        tv_ch_addiction.setTextColor(resources.getColor(R.color.grey1))

        when(habit.habitCategory) {
            HabitCategory.Health -> {
                ch_btn_health.setBackgroundResource(R.drawable.bg_capsul_stroke_grey4)
                iv_ch_health.setImageResource(R.drawable.ic_ch_health_on)
                tv_ch_heath.setTextColor(resources.getColor(R.color.grey4))
                adapter!!.updateList(Utils.getHealthSuggestionList())
            }
            HabitCategory.finance -> {
                ch_btn_finance.setBackgroundResource(R.drawable.bg_capsul_stroke_grey4)
                iv_ch_finance.setImageResource(R.drawable.ic_ch_finance_on)
                tv_ch_finance.setTextColor(resources.getColor(R.color.grey4))
                adapter!!.updateList(Utils.getFinanceSuggestionList())
            }
            HabitCategory.Productivity -> {
                ch_btn_productivity.setBackgroundResource(R.drawable.bg_capsul_stroke_grey4)
                iv_ch_productivity.setImageResource(R.drawable.ic_ch_productivity_on)
                tv_ch_productivity.setTextColor(resources.getColor(R.color.grey4))
                adapter!!.updateList(Utils.getProductivitySuggestionList())
            }
            HabitCategory.DigitalWellbeign -> {
                ch_btn_digital_wellbeing.setBackgroundResource(R.drawable.bg_capsul_stroke_grey4)
                iv_ch_digital_wellbeing.setImageResource(R.drawable.ic_ch_digital_wellbeing_on)
                tv_ch_digital_wellbeing.setTextColor(resources.getColor(R.color.grey4))
                adapter!!.updateList(Utils.getDigitalWellbeingSuggestionList())
            }
            HabitCategory.Mindfullness -> {
                ch_btn_mindfullness.setBackgroundResource(R.drawable.bg_capsul_stroke_grey4)
                iv_ch_mindfullness.setImageResource(R.drawable.ic_ch_mindfullness_on)
                tv_ch_mindfullness.setTextColor(resources.getColor(R.color.grey4))
                adapter!!.updateList(Utils.getMindfullnessSuggestionList())
            }
            HabitCategory.Addiction -> {
                ch_btn_addiction.setBackgroundResource(R.drawable.bg_capsul_stroke_grey4)
                iv_ch_addiction.setImageResource(R.drawable.ic_ch_addiction_on)
                tv_ch_addiction.setTextColor(resources.getColor(R.color.grey4))
                adapter!!.updateList(Utils.getAddictionSuggestionList())
            }
        }
    }

    fun searchHabit() {

    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        habit.habitTitle = s.toString()
    }

    ////////////
    ///// EVENTS
    ////////////

    @Subscribe (threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event : SelectedHabitSuggestionEvent) {
        ch_et_habit_name.setText(event.suggestion)
        habit.habitTitle = event.suggestion
        ch_et_habit_name.setSelection(ch_et_habit_name.text.length)
    }

}
