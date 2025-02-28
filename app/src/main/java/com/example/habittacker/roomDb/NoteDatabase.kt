package com.example.habittacker.roomDb


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [NoteData::class], version = 1, exportSchema = false)
abstract class NoteDatabase:RoomDatabase(){
    abstract fun notesdao():NotesDao
    companion object {
        @Volatile
        var Instance: NoteDatabase? = null
        fun getDatabase(context: Context): NoteDatabase {
            val tempInstances = Instance
            if (tempInstances != null) return tempInstances
            synchronized(this) {
                val roomDb = Room.databaseBuilder(context, NoteDatabase::class.java, "Notes")
                    .allowMainThreadQueries().build()
                Instance = roomDb
                return roomDb
            }
        }
    }
}