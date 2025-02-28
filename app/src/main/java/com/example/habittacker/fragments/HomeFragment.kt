package com.example.habittacker.fragments

import android.app.Application
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittacker.AllHabitActivity
import com.example.habittacker.R
import com.example.habittacker.Utils
import com.example.habittacker.adapter.DateAdapter
import com.example.habittacker.adapter.HabitAdapter
import com.example.habittacker.databinding.AddHabitBinding
import com.example.habittacker.databinding.FragmentHomeBinding
import com.example.habittacker.models.date
import com.example.habittacker.models.habit
import com.example.habittacker.viewmodel.ViewModelAuth
import com.example.habittacker.viewmodel.viewModelHabits
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

open class HomeFragment : Fragment(), DateAdapter.DateClickListener {
    private lateinit var binding: FragmentHomeBinding
    private val viewModelAuth: ViewModelAuth by viewModels()
    private val viewModelHabits: viewModelHabits by viewModels()
    var piehours: Int = 24
    var cdate: Int? = null
    var cmonth: String? = null

    private lateinit var mutableHabitList: MutableList<habit>
     var list = mutableListOf<date>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)

        binding.progressBarChart.visibility = View.VISIBLE
        binding.progressBarRv.visibility = View.VISIBLE

        binding.radio24Button.isChecked = true
        getDate()
        gethabit()
        onRadioButtonClick()
        setupPieChart()
        loadPieChartData(piehours)
        onAddHabitButtonClick()


        setDateRecyclerView()
        getDetailHabit()

        binding.pieChart.invalidate()
        return binding.root
    }

    private fun getDetailHabit(){
        binding.seeDetsTxt.setOnClickListener {
            startActivity(Intent(requireContext(),AllHabitActivity::class.java))
        }
    }
    private fun setDateRecyclerView() {
        binding.dateRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.dateRecyclerView.adapter = DateAdapter(list, this)
        binding.dateRecyclerView.setHasFixedSize(true)
    }

     fun getDate() {
        val calender = Calendar.getInstance()
        val currentYear = Calendar.getInstance()[Calendar.YEAR]
        val currentMonth = Calendar.getInstance()[Calendar.MONTH]
        val currentDate = Calendar.getInstance()[Calendar.DATE]
        cdate = currentDate
        val monthName = calender.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
        cmonth=monthName

        calender.set(Calendar.YEAR, currentYear)
        calender.set(Calendar.MONTH, currentMonth)
        calender.set(Calendar.DAY_OF_MONTH, 1)
        val daysInMonth = calender.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (day in 1..daysInMonth) {
            calender.set(Calendar.DAY_OF_MONTH, day)

            val dayOfWeek: String =
                calender.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()).toString()
            val dayOfMonth = calender.get(Calendar.DAY_OF_MONTH)
            val monthName = calender.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())

            list.add(date(dayOfWeek, dayOfMonth, monthName))
        }
    }

    override fun onDateClicked(date: date) {
        gethabitList(date)
        gethabit()

    }

    fun gethabit() {
        mutableHabitList = mutableListOf()

        lifecycleScope.launch {
            viewModelHabits.getListReverse(requireContext()).observe(viewLifecycleOwner, Observer { habit ->
                mutableHabitList.clear()
                for (hab in habit) {
                    val date = SimpleDateFormat("dd").format(hab.timestamp).toInt()
                    val dateMonth = SimpleDateFormat("MMM").format(hab.timestamp)
                    Log.d("date", "$date & ${cdate} $dateMonth")
                    if (cdate == date && cmonth==dateMonth) {
                        mutableHabitList.add(hab)
                    }
                }
                if (mutableHabitList.isNotEmpty()) {
                    binding.homerecyclerView.visibility=View.VISIBLE
                    binding.infohabitTxt2.visibility=View.GONE


                    binding.homerecyclerView.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    binding.homerecyclerView.adapter = HabitAdapter(mutableHabitList)
                    binding.homerecyclerView.setHasFixedSize(true)
                    binding.progressBarRv.visibility = View.GONE
                } else {
                    Utils.makeToast(requireContext(), "No data available")
                    binding.homerecyclerView.visibility=View.GONE
                    binding.infohabitTxt2.visibility=View.VISIBLE
                }
            })
        }
    }

    fun gethabitList(cudate: date) {
        cdate = cudate.date
        cmonth=cudate.month

        Log.d("date", " ${cdate} $cmonth")



    }

    fun onAddHabitButtonClick() {
        binding.addHabitBtn.setOnClickListener {
            var alertDialog: AlertDialog? = null
            var habitBinding = AddHabitBinding.inflate(layoutInflater)
            alertDialog = AlertDialog.Builder(requireContext())
                .setView(habitBinding.root)
                .create()
            alertDialog.show()

            habitBinding.addHabitButton.setOnClickListener {
                val habit = habitBinding.habitTxt.text.toString()
                val hours = habitBinding.hoursTxt.text.toString()

                if (habit.isNotEmpty() || hours.isNotEmpty()) {
                    val habits = habit("", habit, hours, 0L)
                    viewModelHabits.addhabits(habits, requireContext())
                    alertDialog.dismiss()
                }
                loadPieChartData(piehours)
            }
        }
    }

    private fun setupPieChart() {
        binding.pieChart.apply {
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            holeRadius = 10f
            transparentCircleRadius = 61f
            setUsePercentValues(true)
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(12f)
            setDrawEntryLabels(false)

            description.isEnabled = false

            val legend = legend
            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.setDrawInside(false)
            legend.isEnabled = true
        }
    }

    fun loadPieChartData(h: Int) {
        val entries = ArrayList<PieEntry>()

        viewModelHabits.getListReverse(requireContext())
            .observe(viewLifecycleOwner, Observer { items ->
                val habitHoursMap = mutableMapOf<String, Int>()
                var hhTH = 0
                for (item in items) {
                    val habitName = item.habit.toString().lowercase() ?: continue
                    val hours = item.hours?.toInt() ?: continue

                    if (hhTH <= h) {
                        habitHoursMap[habitName] = habitHoursMap.getOrDefault(habitName, 0) + hours
                        hhTH += hours
                    }
                }

                for ((habitName, summedHours) in habitHoursMap) {
                    entries.add(PieEntry(summedHours.toFloat(), habitName))
                }

                val dataSet = PieDataSet(entries, "Habit").apply {
                    setColors(Utils.colors)
                }

                val data = PieData(dataSet).apply {
                    setDrawValues(true)
                    setValueTextSize(12f)
                    setValueTextColor(Color.BLACK)
                }

                binding.pieChart.data = data
                binding.pieChart.invalidate()
                binding.progressBarChart.visibility = View.GONE
            })
    }

    private fun onRadioButtonClick() {
        binding.radioGroup.setOnCheckedChangeListener { group, id ->
            when (id) {
                R.id.radio24Button -> {
                    binding.radio24Button.isChecked = true
                    binding.radio30Button.isChecked = false
                    binding.radio10Button.isChecked = false
                    piehours = HOURS_24
                    loadPieChartData(piehours)
                    binding.infoTxt.text = "24 Hours PieChart"
                }

                R.id.radio10Button -> {
                    binding.radio24Button.isChecked = false
                    binding.radio30Button.isChecked = false
                    binding.radio10Button.isChecked = true
                    piehours = HOURS_10_DAYS
                    loadPieChartData(piehours)
                    binding.infoTxt.text = "10 Days PieChart"
                }

                R.id.radio30Button -> {
                    binding.radio24Button.isChecked = false
                    binding.radio30Button.isChecked = true
                    binding.radio10Button.isChecked = false
                    piehours = HOURS_30_DAYS
                    loadPieChartData(piehours)
                    binding.infoTxt.text = "30 Days PieChart"
                }
            }
        }
    }

    companion object {
        const val HOURS_24 = 24
        const val HOURS_10_DAYS = 240
        const val HOURS_30_DAYS = 720
    }
}
