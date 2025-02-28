package com.example.habittacker.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.habittacker.Utils
import com.example.habittacker.models.habit
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class viewModelHabits : ViewModel() {



    fun addhabits(habit: habit, context: Context) {
        val uid = Utils.getCurrentUser()
        val databaseRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("habits")

        val newKey = databaseRef.push().key
        habit.id=newKey
        habit.timestamp= System.currentTimeMillis()

        if (newKey != null) {
            databaseRef.child(newKey).setValue(habit)
                .addOnSuccessListener {
                    Utils.successToast(context, "Habit added successfully")
                }
                .addOnFailureListener {
                    Utils.makeToast(context, "Something went wrong")
                }
        } else {
            Utils.makeToast(context, "Failed to generate key")
        }


    }


    fun updatehabit(newKey:String,habit: habit, context: Context) {
        val uid = Utils.getCurrentUser()
        val databaseRef = FirebaseDatabase.getInstance().getReference("users").child(uid).child("habits")

        val habitinfo= mapOf("id" to newKey,"habit" to habit.habit,"hours" to habit.hours)

        if (newKey != null) {
            databaseRef.child(newKey).updateChildren(habitinfo)
                .addOnSuccessListener {



                    Utils.successToast(context, "Habit updated successfully")
                }
                .addOnFailureListener {
                    Utils.makeToast(context, "Something went wrong")
                }
        } else {
            Utils.makeToast(context, "Failed to generate key")
        }



    }
    fun deletehabit(newKey:String, context: Context) {

        val uid = Utils.getCurrentUser()
        val database = FirebaseDatabase.getInstance().getReference("users").child(uid).child("habits")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                database.child(newKey).removeValue().addOnSuccessListener {

                    Utils.successToast(context, "Habit deleted successfully")

                }.addOnFailureListener {
                    Utils.makeToast(context, "Something went wrong")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Utils.makeToast(context, "Failed to read data $error")
            }
        })

    }

    fun getList(): LiveData<MutableList<habit>> {
        val uid = Utils.getCurrentUser()
        var habitlistLiveData = MutableLiveData<MutableList<habit>>()
        val database =
            FirebaseDatabase.getInstance().getReference("users").child(uid).child("habits")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<habit>()
                for (childsnapshot in snapshot.children) {
                    val item = childsnapshot.getValue(habit::class.java)
                    item?.let { lists.add(it) }
                }
                habitlistLiveData.value = lists

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
        return habitlistLiveData


    }


    fun getListReverse(context: Context): LiveData<MutableList<habit>> {
        val uid = Utils.getCurrentUser()
        val habitlistLiveData = MutableLiveData<MutableList<habit>>()
        val database = FirebaseDatabase.getInstance().getReference("users").child(uid).child("habits")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<habit>()

                for (childsnapshot in snapshot.children) {
                    val item = childsnapshot.getValue(habit::class.java)
                    item?.let { lists.add(it) }
                }

                val reversedList = lists.reversed().toMutableList()
                habitlistLiveData.value = reversedList
            }

            override fun onCancelled(error: DatabaseError) {
                Utils.makeToast(context, "Failed to read data $error")
            }
        })

        return habitlistLiveData
    }

}