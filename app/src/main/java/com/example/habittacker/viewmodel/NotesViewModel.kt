package com.example.habittacker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.habittacker.roomDb.NoteData
import com.example.habittacker.roomDb.NoteDatabase
import com.example.habittacker.roomDb.NotesDao

class NotesViewModel(application: Application):AndroidViewModel(application) {
    val notesDao:NotesDao=NoteDatabase.getDatabase(application).notesdao()
    fun addnotes(noteData: NoteData){
        notesDao.addNote(noteData)
    }
     suspend fun deleteNotes(id:String){
        notesDao.deleteNote(id)
        }
   fun getNotes(): LiveData<List<NoteData>> {
            return notesDao.getNotes()
    }
   suspend fun editNote(description :String,id:String){
        return notesDao.edit(description,id)
    }
}