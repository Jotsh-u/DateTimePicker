package com.mydatetimepicker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.mydatetimepicker.databinding.ItemDateLayoutBinding
import com.mydatetimepicker.utill.getZeroPrefix
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AdapterMeridianList(
    private val list: ArrayList<String>
) : Adapter<ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDateLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val value = list[position]

        with(holder){
            binding.tv.text = value
        }
    }
}