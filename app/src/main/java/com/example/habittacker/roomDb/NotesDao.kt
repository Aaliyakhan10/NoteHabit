package com.example.habittacker.roomDb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.mikephil.charting.components.Description

@Dao
interface NotesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNote(noteData: NoteData)
    @Query("DELETE FROM Notes WHERE ID=:id")
     suspend fun deleteNote(id:String)
    @Query("SELECT id,imagePath,description FROM Notes")
    fun getNotes():LiveData<List<NoteData>>
    @Query("UPDATE Notes SET description=:description WHERE ID=:id")
    suspend fun edit(description:String,id: String)

}