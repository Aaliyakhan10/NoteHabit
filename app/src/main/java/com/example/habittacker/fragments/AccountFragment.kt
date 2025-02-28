package com.example.habittacker.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.habittacker.Utils
import com.example.habittacker.databinding.FragmentAccountBinding
import com.example.habittacker.viewmodel.ViewModelAuth
import kotlinx.coroutines.flow.MutableStateFlow

class AccountFragment : Fragment() {
    private lateinit var binding: FragmentAccountBinding
    private val viewModelAuth:ViewModelAuth by viewModels()
    private val _email= MutableStateFlow<String>("User")
    val email=_email

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentAccountBinding.inflate(layoutInflater, container, false)
        Log.d("account","started")
        getInfo()
        onResetButtonClick()


        return binding.root
    }

    private fun onResetButtonClick() {
        binding.reserPassBtn.setOnClickListener {
            viewModelAuth.resetPassword(requireContext(),email.value)
        }
    }

    fun getInfo() {
        var name:String
        var email:String
        viewModelAuth.getUserdata{Name,Email,pass->
            binding.nameDisTv.text="Name:$Name"
            binding.emailDisTv.text="Email:$Email"
            name=Name
            email=Email
            _email.value=email
            Log.d("account","started with $name $email")
            onUpdateButtonClick(name,email)

        }
    }

    private fun onUpdateButtonClick(name: String, email: String) {
        Log.d("account","started change")
        binding.changeInfoBtn.setOnClickListener {
            Log.d("account","started changong info")
            binding.editll.visibility=View.VISIBLE
            binding.nameEdit.setText(name)
            Utils.warningDialog(requireContext(),"Are you sure to change Info","Yes")
            binding.saveInfoBtn.setOnClickListener {
                val updatedName=binding.nameEdit.text.toString()

                if(updatedName.isNotEmpty()){
                    viewModelAuth.updateUserData(requireContext(),updatedName,email)
                    binding.editll.visibility=View.GONE
                    getInfo()
                }
            }
        }

    }


    companion object {

    }
}