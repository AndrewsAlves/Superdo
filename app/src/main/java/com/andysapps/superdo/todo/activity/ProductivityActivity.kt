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
import com.andysapps.superdo.todo.manager.FirestoreManager
import com.andysapps.superdo.todo.manager.TaskOrganiser
import com.andysapps.superdo.todo.model.esprit.EspritStatistics
import com.andysapps.superdo.todo.model.esprit.EspritStatPoint
import com.hadiidbouk.charts.BarData
import com.yarolegovich.discretescrollview.transform.Pivot
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import kotlinx.android.synthetic.main.activity_productivity.*

class ProductivityActivity : AppCompatActivity() , AdapterView.OnItemSelectedListener {

    val statType = arrayOf("This Week", "This Month", "Last Week",  "Last Month")
    var selectedStat = statType[0]

    var espritStatistics : EspritStatistics? = null

    var selectedList = 0

    var nextSegment = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_productivity)
        espritStatistics = EspritStatistics()
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
            selectedList = it
            updateEspritStat(espritStatistics!!.getEspritStatPoints()[it])
        }

       /* productivity_iv_next_previous.setOnClickListener {
            nextSegment = !nextSegment
            ChartProgressBarMain.selectBar(0)
            updateView()
        }*/
    }

    fun updateView() {
        productivity_tv_total_points.text = FirestoreManager.getInstance().user.espritPoints.toString()


        /*if (selectedStat == statType[1] || selectedStat == statType[3]) {
            productivity_iv_next_previous.visibility = View.VISIBLE
            if (nextSegment) {
                for (i in 15 until espritStatistics!!.getBarData().size) {
                    barDatalist.add(espritStatistics!!.getBarData()[i])
                }
            } else {
                for (i in 0..14) {
                    barDatalist.add(espritStatistics!!.getBarData()[i])
                }
            }
        } else {
            barDatalist = espritStatistics!!.getBarData()
            productivity_iv_next_previous.visibility = View.GONE
        }*/


        if (selectedStat == statType[1] || selectedStat == statType[3]) {
            ll_month_date_title.visibility = View.VISIBLE
        } else {
            ll_month_date_title.visibility = View.GONE
        }

        var max = 0.0f

        for (bardate in espritStatistics!!.getBarData()) {
            if (bardate.barValue > max) {
                max = bardate.barValue
            }
        }

        if (max <= 0.0f) {
            max = 5.0f
        }

        ChartProgressBarMain.setMaxValue(max)
        ChartProgressBarMain.setDataList(espritStatistics!!.getBarData())
        ChartProgressBarMain.build()
    }

    fun updateEspritStat(espritStatPoint: EspritStatPoint) {
        productivity_tv_esprit_earned_count.text = espritStatPoint.totalEsprit.toString()
        productivity_tv_total_tasks_count.text = espritStatPoint.totalTasksCompleted.toString()
        productivity_tv_statetype.text = espritStatPoint.date.superDateString
    }

    fun updateEsprtStatForFullChart() {
        productivity_tv_esprit_earned_count.text = espritStatistics!!.totalEsprit.toString()
        productivity_tv_total_tasks_count.text = espritStatistics!!.totalTasksCompleted.toString()
        productivity_tv_statetype.text = selectedStat
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        ChartProgressBarMain.selectBar(0)
        selectedStat = statType[position]
        espritStatistics = TaskOrganiser.getInstance().getEspritStatistics(selectedStat)
        updateView()
        updateEsprtStatForFullChart()
    }
}
