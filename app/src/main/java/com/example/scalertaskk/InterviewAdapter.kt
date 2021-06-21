package com.example.scalertaskk

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_interview.view.*


class InterviewAdapter(
    var list: List<InterviewModel>,
    var activity: Context,
) :
    RecyclerView.Adapter<InterviewAdapter.MyViewHolder>() {


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InterviewAdapter.MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_interview, parent, false)
        )
    }

    override fun onBindViewHolder(holder: InterviewAdapter.MyViewHolder, position: Int) {

        var itemmodel = list[position]
        holder.itemView.apply {
            tv_name.text = "${itemmodel.emails.emails[0]}"
            tv_time.text = "${itemmodel.date} (${itemmodel.startTime} to ${itemmodel.endTime})"

            iv_edit.setOnClickListener(View.OnClickListener {
                var intent = Intent(context,CreateInterviewActivity::class.java)
                intent.putExtra("data",itemmodel)
                context.startActivity(intent)
            })
        }
    }

    override fun getItemCount(): Int = list.size

}
