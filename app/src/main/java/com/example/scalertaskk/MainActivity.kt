package com.example.scalertaskk

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    var interviewAdapter:InterviewAdapter?=null
    var list:List<InterviewModel> = arrayListOf()
    var database : AppDatabase?=null
    var interviewDao : InterviewDao?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar.visibility+View.VISIBLE
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "interview_db"
        ).build()
        interviewDao = database!!.interviewDao()
        interviewAdapter = InterviewAdapter(list,this)
        rv_interview.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = interviewAdapter
        }
        lifecycleScope.launch {
            withContext(Dispatchers.IO)
            {
                val date: String =
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                list = interviewDao!!.loadAll(date)
                interviewAdapter!!.list = list
                interviewAdapter!!.notifyDataSetChanged()
                progressBar.visibility=View.GONE
                Log.e("Size","${list.size}")
            }
            if (list.size<1)
            {
                empty_state.visibility= View.VISIBLE
            }
            else
            {
                empty_state.visibility= View.GONE
            }
        }

        btn_add.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this,CreateInterviewActivity::class.java))
        })
    }
}