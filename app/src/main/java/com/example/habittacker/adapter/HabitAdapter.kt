package com.example.habittacker.adapter

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.habittacker.R
import com.example.habittacker.Utils
import com.example.habittacker.databinding.AddHabitBinding
import com.example.habittacker.databinding.HabitRecyclerViewBinding
import com.example.habittacker.fragments.HomeFragment
import com.example.habittacker.models.habit
import com.example.habittacker.viewmodel.viewModelHabits
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.math.floor

class HabitAdapter(val mutableHabitList: MutableList<habit>) : RecyclerView.Adapter<HabitAdapter.ViewHolderHabit>() {
    private val fragmentHome = HomeFragment()

    class ViewHolderHabit(val binding: HabitRecyclerViewBinding):RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHabit {
       return ViewHolderHabit(HabitRecyclerViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return mutableHabitList.size
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: ViewHolderHabit, position: Int) {
        holder.binding.apply {
            habitText.text=mutableHabitList[position].habit
            hoursText.text="Hours:"+mutableHabitList[position].hours
            val date = SimpleDateFormat("dd MMM yyyy  hh:mm a").format(Date(mutableHabitList[position].timestamp))
            datetxt.text = date.toString()

//            val size=Utils.colors.size
//            val colorInd= floor(Math.random()*size)
//
//            llView.setBackgroundColor(Utils.colors[colorInd.toInt()])



        }
        holder.binding.optionRv.setOnClickListener{
          val viewmodelHabits=viewModelHabits()
            val popupMenu=PopupMenu(holder.itemView.context,holder.binding.optionRv)
            val inflater=popupMenu.menuInflater
             inflater.inflate(R.menu.rv_menu,popupMenu.menu)


            popupMenu.show()

            popupMenu.setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.edit->{
                        var alertDialog: AlertDialog? = null
                        var habitBinding = AddHabitBinding.inflate(LayoutInflater.from(holder.itemView.context))
                        alertDialog = AlertDialog.Builder(holder.itemView.context)
                            .setView(habitBinding.root)
                            .create()
                        alertDialog.show()
                        habitBinding.hbtxt.hint=mutableHabitList[position].habit
                        habitBinding.hbtxt.isEnabled=false

                        habitBinding.hrstxt.hint=mutableHabitList[position].hours
                        habitBinding.addHabitButton.setOnClickListener {
                            val habit = mutableHabitList[position].habit
                            val hours = habitBinding.hoursTxt.text.toString()

                            if (habit!!.isNotEmpty() || hours.isNotEmpty()) {


                                val newKey=mutableHabitList[position].id
                                val habits = habit(mutableHabitList[position].id,habit, hours)
                                viewmodelHabits.updatehabit(newKey.toString(),habits, holder.itemView.context)
                                alertDialog.dismiss()

                            }



                        }

                        true

                    }
                    R.id.delete->{
                        val newKey=mutableHabitList[position].id
                        viewmodelHabits.deletehabit(newKey.toString(),holder.itemView.context)


                        true
                    }


                    else -> {
                        false
                    }
                }
            }

            Utils.makeToast(holder.itemView.context,"option click")
        }
    }

    private fun option() {

    }


}