package com.example.habittacker.fragments

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittacker.Utils

import com.example.habittacker.adapter.NotesAdapter
import com.example.habittacker.databinding.AddNoteLayoutBinding
import com.example.habittacker.databinding.FragmentHome2Binding
import com.example.habittacker.roomDb.NoteData
import com.example.habittacker.viewmodel.NotesViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Appendable
import java.security.SecureRandom
import java.util.Base64

class Home2Fragment : Fragment() {
    private lateinit var binding: FragmentHome2Binding
    //private var lits = mutableListOf<NoteData>()
    private lateinit var photoLauncher: ActivityResultLauncher<Intent>
    private lateinit var imageFilePath: String
    private val viewModel: NotesViewModel by viewModels()
    private lateinit var bitmap: Bitmap

private lateinit var addNoteBinding:AddNoteLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHome2Binding.inflate(layoutInflater)


        getNotesLits()


        photoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri = result.data?.data ?: return@registerForActivityResult
                addNoteBinding.uploadimgView.visibility=View.VISIBLE
                addNoteBinding.uploadimgView.setImageURI(selectedImageUri)
                Utils.successToast(requireContext(),"Image Uploaded successfully")

                val _bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, selectedImageUri)
                 bitmap=_bitmap
                // Save the image to internal storage
                val imageFile = File(requireContext().filesDir, "image_${System.currentTimeMillis()}.jpg")
                val outputStream = FileOutputStream(imageFile)
                _bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)



                outputStream.close()

                imageFilePath = imageFile.absolutePath




            }
        }

        onAddNoteButtonClick()

        return binding.root
    }
   private fun checkCancel():Boolean{
       var iscancel:Boolean=true
        if(addNoteBinding.notesTv.text.toString().isNotEmpty()){
           iscancel= false
        }
       return iscancel
    }
    fun onAddNoteButtonClick() {
        binding.addBtn.setOnClickListener {
            var iscancel=true
            var dialog: AlertDialog? = null
             addNoteBinding = AddNoteLayoutBinding.inflate(layoutInflater)

            dialog = AlertDialog.Builder(requireContext())
                .setView(addNoteBinding.root)
                .create()
            iscancel=checkCancel()
            dialog.setCancelable(iscancel)


            addNoteBinding.uploadImg.setOnClickListener {
                Log.d("photo", "upload button click")

                if (ContextCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED) {

                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    photoLauncher.launch(intent)
                    dialog.setCancelable(false)


                } else {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        1
                    )
                }
            }





            addNoteBinding.saveBtn.setOnClickListener {
                val noteText = addNoteBinding.notesTv.text.toString()

                // If no image is selected, show a warning
                if (!::imageFilePath.isInitialized) {
                    Utils.warningDialog(requireContext(),"Please select the image","Okay")
                    return@setOnClickListener
                }

                val id = generateRandomKey()
                val note = NoteData(id, imagePath = imageFilePath, description = noteText)
                addNotes(note, dialog)
                Log.d("note", "Added")
            }
            dialog.show()
        }
    }

    private fun addNotes(note: NoteData, dialog: AlertDialog) {
        lifecycleScope.launch {
            viewModel.addnotes(note)
        }
        Utils.successToast(requireContext(),"Note Added Successfully")
        dialog.dismiss()
        getNotesLits()
    }

    private fun getNotesLits() {

        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            viewModel.getNotes().observe(viewLifecycleOwner, Observer {

                val notesAdapter = NotesAdapter(it.reversed().toMutableList(),requireActivity().application)
                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.recyclerView.adapter = notesAdapter
                binding.recyclerView.setHasFixedSize(true)
                notesAdapter.notifyDataSetChanged()



            })
            binding.progressBar.visibility = View.GONE
        }


    }



    fun generateRandomKey(length: Int = 16): String {
        val secureRandom = SecureRandom()
        val randomBytes = ByteArray(length)
        secureRandom.nextBytes(randomBytes)
        return Base64.getEncoder().encodeToString(randomBytes)
    }
}
