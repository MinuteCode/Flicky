package com.minutecode.flicky.ui.result_detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.minutecode.flicky.R

class ResultDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.result_detail_activity)

        if (savedInstanceState == null) {
            val movieBundle = Bundle()
            movieBundle.putParcelable("movie", intent.getParcelableExtra("movie"))
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ResultDetailFragment.newInstance(arguments = movieBundle), "result_detail_fragment")
                .commitNow()
        }
    }
}
