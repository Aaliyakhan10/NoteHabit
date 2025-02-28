package com.example.habittacker.roomDb

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Notes")
data class NoteData(
    @PrimaryKey
    val id:String,
    val imagePath: String? = null,
    var description:String

)

