package com.mydatetimepicker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.mydatetimepicker.databinding.ItemDateLayoutBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AdapterDateList(
    private val list: ArrayList<Calendar>
) : Adapter<ViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDateLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val value = list[position]
        val sdf = SimpleDateFormat("EEE dd MMM yyyy", Locale.getDefault())
        with(holder){
//            Log.e("DATES","Dates>> : ${sdf.format(value.time)}")
            binding.tv.text = sdf.format(value.time)
        }
    }
}

class ViewHolder(val binding: ItemDateLayoutBinding) : RecyclerView.ViewHolder(binding.root)