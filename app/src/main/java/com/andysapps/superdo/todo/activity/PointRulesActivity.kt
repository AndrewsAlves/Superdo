package com.andysapps.superdo.todo.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.andysapps.superdo.todo.R
import kotlinx.android.synthetic.main.activity_point_rules.*

class PointRulesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point_rules)

        ib_close_point_rules.setOnClickListener {
            onBackPressed()
        }
    }
}
