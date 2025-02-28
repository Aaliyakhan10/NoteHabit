package com.example.habittacker.adapter

import android.app.Application
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.habittacker.databinding.RvDatesBinding
import com.example.habittacker.models.date
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DateAdapter(
    private val list: MutableList<date>,
    private val listener: DateClickListener
) : RecyclerView.Adapter<DateAdapter.viewHolder>() {

    interface DateClickListener {
        fun onDateClicked(date: date)
    }

    class viewHolder(val binding: RvDatesBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        return viewHolder(RvDatesBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val current = list[position]
        holder.binding.apply {
            dateTx.text = current.date.toString()
            datTxt.text = current.day
            monthTxt.text = current.month
        }

        holder.binding.root.setOnClickListener {
            listener.onDateClicked(current)
        }
    }
}
