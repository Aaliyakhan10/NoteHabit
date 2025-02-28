package com.example.habittacker.adapter

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.habittacker.R
import com.example.habittacker.Utils
import com.example.habittacker.databinding.AddNoteLayoutBinding
import com.example.habittacker.databinding.ImageViewBinding
import com.example.habittacker.databinding.NotesRvBinding
import com.example.habittacker.roomDb.NoteData
import com.example.habittacker.viewmodel.NotesViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NotesAdapter(val list: MutableList<NoteData>, val app: Application) :
    RecyclerView.Adapter<NotesAdapter.NotesViewHolderClass>() {

    class NotesViewHolderClass(val binding: NotesRvBinding) :
        RecyclerView.ViewHolder(binding.root) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolderClass {

        return NotesViewHolderClass(
            NotesRvBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: NotesViewHolderClass, position: Int) {
        val note = list[position]
        holder.binding.apply {
            noteDes.text = note.description



            if (note.imagePath?.isNotEmpty() == true) {

                noteImg.visibility = View.VISIBLE
                val bitmap = BitmapFactory.decodeFile(note.imagePath)
                noteImg.setImageBitmap(bitmap)
                noteImg.setOnClickListener {
                    seeImage(holder.itemView.context,bitmap)
                }

            }

            optBtn.setOnClickListener {
                val noteViewModel = NotesViewModel(app)
                val popupMenu = PopupMenu(holder.itemView.context, holder.binding.optBtn)
                val inflater = popupMenu.menuInflater
                inflater.inflate(R.menu.rv_menu, popupMenu.menu)
                popupMenu.show()

                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.delete -> {
                            GlobalScope.launch {
                                noteViewModel.deleteNotes(note.id)
                            }
                            Utils.makeToast(holder.itemView.context,"Note deleted successfully")

                            true
                        }

                        R.id.edit -> {
                            var dialog: AlertDialog? = null
                            val addNoteBinding =
                                AddNoteLayoutBinding.inflate(LayoutInflater.from(holder.itemView.context))

                            dialog = AlertDialog.Builder(holder.itemView.context)
                                .setView(addNoteBinding.root)
                                .create()
                            dialog.show()
                            addNoteBinding.notesTv.setText(note.description)
                            addNoteBinding.uploadImg.visibility = View.GONE
                            addNoteBinding.uploadimgView.visibility = View.VISIBLE
                            val bitmap = BitmapFactory.decodeFile(note.imagePath)
                            addNoteBinding.uploadimgView.setImageBitmap(bitmap)
                            addNoteBinding.saveBtn.setOnClickListener {

                                val noteText = addNoteBinding.notesTv.text.toString()
                                editnote(noteText, note.id, dialog, noteViewModel,holder.itemView.context)

                            }



                            true
                        }

                        else -> {
                            false
                        }
                    }
                }
            }

        }
    }

    private fun seeImage(context: Context, bitmap: Bitmap) {
        val imageBinding=ImageViewBinding.inflate(LayoutInflater.from(context))
        var dialog:AlertDialog?= null
        dialog=AlertDialog.Builder(context)
            .setView(imageBinding.root)
            .create()
        imageBinding.imageViewOnly.setImageBitmap(bitmap)
            dialog.show()
    }

    private fun editnote(
        noteText: String,
        id: String,
        dialog: AlertDialog,
        noteViewModel: NotesViewModel,
        context: Context
    ) {
        GlobalScope.launch {
            noteViewModel.editNote(noteText, id)
        }
        Utils.normalDialog(context,"Note edited successfully")

        dialog.dismiss()
    }

    private fun byteArrayToBitmap(image: ByteArray?): Any {
        return BitmapFactory.decodeByteArray(image, 0, image!!.size)

    }


}