package com.example.habittacker.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.habittacker.MainActivity
import com.example.habittacker.R
import com.example.habittacker.Utils
import com.example.habittacker.databinding.FragmentSingInBinding
import com.example.habittacker.models.User
import com.example.habittacker.viewmodel.ViewModelAuth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.shashank.sony.fancytoastlib.FancyToast


class SingInFragment : Fragment() {
    private lateinit var binding: FragmentSingInBinding
    private lateinit var users: FirebaseUser
    private val viewModel: ViewModelAuth by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSingInBinding.inflate(layoutInflater, container, false)

        onSignInButtonClick()
        onLoginButtonCLick()

        return binding.root
    }


    private fun onLoginButtonCLick() {
        binding.loginin.setOnClickListener {
            findNavController().navigate(R.id.action_singIn_to_loginFragment)
        }
    }

    private fun onSignInButtonClick() {
        binding.signinBtn.setOnClickListener {
            val email = binding.emailTxt.text.toString()
            val pass = binding.passwordTxt.text.toString()
            val cpass = binding.confirmPasswordTxt.text.toString()
            val name = binding.nameTxt.text.toString()

            if (email.isNotEmpty() || pass.isNotEmpty() || cpass.isNotEmpty() || name.isNotEmpty() || (pass == cpass)) {
                Utils.getInstance().createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userid = Utils.getCurrentUser()
                            val user = User(name, email, pass, userid)
                            viewModel.setUserData(user)
                            

                            users = Utils.getInstance().currentUser!!

                            users.sendEmailVerification().addOnSuccessListener {

                                Utils.normalDialog(
                                    requireContext(),
                                    "Verify your email by clicking on link send on your email "
                                )
                                Utils.successToast(requireContext(), "Account Created")
                                findNavController().navigate(R.id.action_singIn_to_loginFragment)

                            }
                                .addOnFailureListener {
                                    Utils.makeToast(requireContext(), "Error")
                                    Log.e(
                                        "Sign",
                                        "Error signing in with email link",
                                        task.exception
                                    )
                                }

                        } else {
                            if (users.email == email) {
                                Utils.normalDialog(
                                    requireContext(),
                                    "Already have account Please Login"
                                )
                                findNavController().navigate(R.id.action_singIn_to_loginFragment)
                            }
                            Utils.makeToast(requireContext(), "Failed to create Account")
                            Log.e("Sign", "Error signing in with email link", task.exception)
                        }

                    }
                    .addOnFailureListener {
                        Log.e("singIn", "Error while SingIn")
                    }
            }

        }

    }


    companion object {

    }
}