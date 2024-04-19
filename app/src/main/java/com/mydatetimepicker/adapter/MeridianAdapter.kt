package com.mydatetimepicker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.mydatetimepicker.R
import com.mydatetimepicker.utill.DateViewHolder
import com.mydatetimepicker.utill.dpToPx
import com.mydatetimepicker.utill.spToPx
import java.util.*

class MeridianAdapter(
    val meridiem: ArrayList<String>,
    private val fontSize: Int,
    private val dividerHeight: Int
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = DateViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_date_layout,
                parent,
                false
            )
        )
        view.binding.parent.layoutParams.height =
            dividerHeight.dpToPx(view.binding.tv.context).toInt()
        view.binding.tv.textSize = fontSize.spToPx(view.binding.tv.context)
        return view
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as DateViewHolder

        if (meridiem[position].isNotEmpty())
            viewHolder.binding.tv.text = meridiem[position]
        else
            viewHolder.binding.tv.text = ""
    }


    override fun getItemCount() = meridiem.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

}