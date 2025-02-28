package com.example.habittacker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.wifi.hotspot2.pps.HomeSp
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.habittacker.adapter.ViewPagerAdapter
import com.example.habittacker.databinding.ActivityMainBinding
import com.example.habittacker.fragments.Home2Fragment
import com.example.habittacker.fragments.HomeFragment
import com.example.habittacker.viewmodel.ViewModelAuth
import com.example.habittacker.viewmodel.viewModelHabits
import com.google.android.material.tabs.TabLayoutMediator
import com.marsad.stylishdialogs.StylishAlertDialog
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModelAuth: ViewModelAuth by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fragmentList= arrayListOf(HomeFragment(),Home2Fragment())
        binding.apply {
            viewPager.adapter=ViewPagerAdapter(fragmentList,this@MainActivity.supportFragmentManager,lifecycle)

           TabLayoutMediator(tabLayout,viewPager){tab,position->
               when(position){
                   0->{
                       tab.text="Habit Tracks"
                       getInfo()

                   }
                   1->{
                       tab.text="Notes"
                       getInfo()
                   }
               }


           }.attach()
        }
        onMenuButtonClick()
        getInfo()

        val sharedPreferences = getSharedPreferences("Setting", Context.MODE_PRIVATE)
        createNotificationChannel()
        if (!sharedPreferences.getBoolean("isWalkScheduled", false)) {

            sharedPreferences.edit().putBoolean("isWalkScheduled", true).apply()
            scheduleNotificationForWalk()
        }
        if (!sharedPreferences.getBoolean("isWaterScheduled", false)) {

            sharedPreferences.edit().putBoolean("isWaterScheduled", true).apply()
            scheduleNotificationForWater()
        }
        if (!sharedPreferences.getBoolean("isMeditateScheduled", false)) {

            sharedPreferences.edit().putBoolean("isMeditateScheduled", true).apply()
            scheduleNotificationForMeditate()
        }

    }

    private fun getInfo() {
        viewModelAuth.getUserdata { name, email, pass ->
            binding.unameTxt.text = "Hi,${name}"
        }
    }

    private fun onMenuButtonClick() {
        binding.menuBtn.setOnClickListener {
            getInfo()
            val popupmenu = PopupMenu(this, binding.menuBtn)
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
        finish()
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Habit & Reminder"
            val desc = "Reminders for habits"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(notificationChannel, name, importance).apply {
                description = desc
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }


    private fun scheduleNotificationForWalk() {

        val data = Data.Builder()
            .putString("forwhat", "walk")
            .build()
        val workRequest1 = PeriodicWorkRequest.Builder(scheduleNotification::class.java, 3, TimeUnit.HOURS)
            .setInputData(data)
            .build()
        WorkManager.getInstance(this).enqueue(workRequest1)
    }

    private fun scheduleNotificationForMeditate() {
        val data = Data.Builder()
            .putString("forwhat", "meditate")
            .build()
        val workRequest2 = PeriodicWorkRequest.Builder(scheduleNotification::class.java,12,TimeUnit.HOURS)

            .setInputData(data)
            .build()
        WorkManager.getInstance(this).enqueue(workRequest2)
    }
    private fun scheduleNotificationForWater() {
        val data = Data.Builder()
            .putString("forwhat", "water")
            .build()
        val workRequest3 = PeriodicWorkRequest.Builder(scheduleNotification::class.java, 1, TimeUnit.HOURS)
            .setInputData(data)
            .build()
        WorkManager.getInstance(this).enqueue(workRequest3)
    }



}