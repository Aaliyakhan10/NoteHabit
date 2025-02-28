package com.example.habittacker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittacker.adapter.DateAdapter
import com.example.habittacker.adapter.HabitAdapter
import com.example.habittacker.databinding.ActivityAllHabitBinding
import com.example.habittacker.fragments.HomeFragment
import com.example.habittacker.models.date
import com.example.habittacker.models.habit
import com.example.habittacker.viewmodel.ViewModelAuth
import com.example.habittacker.viewmodel.viewModelHabits
import com.marsad.stylishdialogs.StylishAlertDialog
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class AllHabitActivity : AppCompatActivity() , DateAdapter.DateClickListener{
    private lateinit var binding: ActivityAllHabitBinding
    private val viewModelHabits: viewModelHabits by viewModels()
    private val viewModelAuth: ViewModelAuth by viewModels()
    private var cudate: Int? = null
    private var cumonth: String? = null
    private lateinit var mutableHabitList: MutableList<habit>
    private var lists = mutableListOf<date>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityAllHabitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fragment=HomeFragment()
        fragment.getDate()
        lists=fragment.list

        cumonth=fragment.cmonth
        cudate=fragment.cdate
        setDateRecyclerView()
        gethabit()
        getInfo()

        onMenuButtonClick()






    }
    private fun setDateRecyclerView() {
        binding.dateRecyclerViewAc.layoutManager =LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.dateRecyclerViewAc.adapter = DateAdapter(lists, this)
        binding.dateRecyclerViewAc.setHasFixedSize(true)
    }

    override fun onDateClicked(date: date) {

        gethabitList(date)
        gethabit()

    }
    fun gethabit() {
        mutableHabitList = mutableListOf()

        lifecycleScope.launch {
            viewModelHabits.getListReverse(this@AllHabitActivity).observe(this@AllHabitActivity, Observer { habit ->
                mutableHabitList.clear()
                for (hab in habit) {
                    val date = SimpleDateFormat("dd").format(hab.timestamp).toInt()
                    val dateMonth = SimpleDateFormat("MMM").format(hab.timestamp)
                    Log.d("date", "$date & ${cudate} $dateMonth")
                    if (cudate == date && cumonth==dateMonth) {
                        mutableHabitList.add(hab)
                    }
                }
                if (mutableHabitList.isNotEmpty()) {
                    binding.homerecyclerViewAc.visibility=View.VISIBLE
                    binding.infohabitTxt.visibility=View.GONE
                    binding.homerecyclerViewAc.layoutManager = LinearLayoutManager(this@AllHabitActivity, LinearLayoutManager.VERTICAL, false)
                    binding.homerecyclerViewAc.adapter = HabitAdapter(mutableHabitList)
                    binding.homerecyclerViewAc.setHasFixedSize(true)
                    binding.progressBarRvAc.visibility = View.GONE
                } else {
                    Utils.makeToast(this@AllHabitActivity, "No data available")
                    binding.homerecyclerViewAc.visibility=View.GONE
                    binding.infohabitTxt.visibility=View.VISIBLE
                }
            })
        }
    }

    fun gethabitList(cdate: date) {
        cudate = cdate.date
        cumonth=cdate.month




    }
    private fun getInfo() {
        viewModelAuth.getUserdata { name, email, pass ->
            binding.unameAcTxt.text = "Hi,${name}"
        }
    }
    private fun onMenuButtonClick() {
        binding.menuAcBtn.setOnClickListener {
            getInfo()
            val popupmenu = PopupMenu(this, binding.menuAcBtn)
            val inflater = popupmenu.menuInflater
            inflater.inflate(R.menu.menu, popupmenu.menu)
            popupmenu.show()
            popupmenu.setOnMenuItemClickListener { items ->
                when (items.itemId) {
                    R.id.logout -> {
                        StylishAlertDialog(this , StylishAlertDialog.WARNING)
                            .setTitleText("Are you sure you want to logout?")
                            .setConfirmText("Logout")
                            .setConfirmClickListener(StylishAlertDialog.OnStylishClickListener {
                                logout()
                            })
                            .setCancelButton(
                                "Cancel",
                                StylishAlertDialog::dismissWithAnimation
                            )
                            .show()


                        true
                    }
                    R.id.setting->{
                        startActivity(Intent(this,SettingActivity::class.java))
                        true
                    }

                    else -> {
                        false
                    }
                }

            }
        }
    }




    fun logout() {
        Utils.getInstance().signOut()
        startActivity(Intent(this, SplashActivity::class.java))
    }

}