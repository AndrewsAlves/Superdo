package com.andysapps.superdo.todo.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.andysapps.superdo.todo.Constants
import com.andysapps.superdo.todo.R
import com.andysapps.superdo.todo.adapters.taskrecyclers.RanksRecyclerAdapter
import com.andysapps.superdo.todo.enums.EspritStatType
import com.andysapps.superdo.todo.enums.RepeatType
import com.andysapps.superdo.todo.manager.FirestoreManager
import com.andysapps.superdo.todo.manager.TaskOrganiser
import com.hadiidbouk.charts.BarData
import com.yarolegovich.discretescrollview.transform.Pivot
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import kotlinx.android.synthetic.main.activity_productivity.*

class ProductivityActivity : AppCompatActivity() , AdapterView.OnItemSelectedListener {

    val statType = arrayOf("This Week", "This Month", "Last Week",  "Last Month")
    var selectedStat = statType[0]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productivity)
        initViews()
        updateView()
    }

    fun initViews() {

        /// stat spinner
        val statisticType = ArrayAdapter(baseContext, android.R.layout.simple_spinner_item, statType)
        statisticType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        productivity_stat_spinner.onItemSelectedListener = this
        productivity_stat_spinner.adapter = statisticType

        dsv_ranks.setItemTransformer(ScaleTransformer.Builder().setMaxScale(1.05f)
                .setMinScale(0.8f)
                .setPivotX(Pivot.X.CENTER) // CENTER is a default one
                .setPivotY(Pivot.Y.BOTTOM) // CENTER is a default one
                .build())

        dsv_ranks.adapter = RanksRecyclerAdapter(this, Constants.trophySrc, Constants.trophyPoints)
        dsv_ranks.setSlideOnFling(true)
        dsv_ranks.setSlideOnFlingThreshold(1000)

        ib_point_rules.setOnClickListener {
            startActivity(Intent(this, PointRulesActivity::class.java))
        }

        ib_close_productivity.setOnClickListener {
            onBackPressed()
        }

        var barlist : ArrayList<BarData> = ArrayList()
        var max = 0.0f
        ChartProgressBarMain.setMaxValue(max)
        ChartProgressBarMain.setDataList(barlist)
        ChartProgressBarMain.build()

        ChartProgressBarMain.setOnBarClickedListener {

        }
    }

    fun updateView() {
        productivity_tv_total_points.text = FirestoreManager.getInstance().user.espritPoints.toString()

        if (selectedStat.equals(statType[1]) || selectedStat.equals(statType[3])) {
            ll_month_date_title.visibility = View.VISIBLE
        } else {
            ll_month_date_title.visibility = View.GONE
        }

        var barlist : ArrayList<BarData> = TaskOrganiser.getInstance().getBarData(selectedStat)
        var max = 0.0f

        for (bardate in barlist) {
            if (bardate.barValue > max) {
                max = bardate.barValue
            }
        }

        if (max <= 0.0f) {
            max = 5.0f
        }

        /*for (bardate in barlist) {
            if (bardate.barValue == 0.0f) {
                bardate.barValue = max / 60
            }
        }*/

        ChartProgressBarMain.setMaxValue(max)
        ChartProgressBarMain.setDataList(barlist)
        ChartProgressBarMain.build()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedStat = statType[position]
        updateView()
    }
}
