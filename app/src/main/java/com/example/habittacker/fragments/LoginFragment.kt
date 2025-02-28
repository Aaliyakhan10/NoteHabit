package com.example.habittacker.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.habittacker.MainActivity
import com.example.habittacker.R
import com.example.habittacker.Utils
import com.example.habittacker.databinding.EmailLayoutBinding
import com.example.habittacker.databinding.FragmentLoginBinding
import com.example.habittacker.viewmodel.ViewModelAuth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var firebaseAuth: FirebaseAuth
    private val viewModelAuth: ViewModelAuth by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        onLoginButtonClick()
        onSignInButtonClick()
        onResetButtonClick()

        return binding.root
    }

    private fun onResetButtonClick() {
        binding.resetTv.setOnClickListener {
            val dialog:AlertDialog
            val emailBinding=EmailLayoutBinding.inflate(layoutInflater)
            dialog=AlertDialog.Builder(requireContext())
                .setView(emailBinding.root)
                .create()
                emailBinding.resetPassBtn.setOnClickListener {
                    val email=emailBinding.emailEditTx.text.toString()
                    if(email.isNotEmpty()){
                        viewModelAuth.resetPassword(requireContext(),email)
                        dialog.dismiss()

                    }
                }



                dialog.show()

        }
    }

    private fun onNotVerify() {


    }

    private fun onSignInButtonClick() {
        binding.singin.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_singIn)
        }

    }

    private fun onLoginButtonClick() {
        binding.loginBtn.setOnClickListener {
            val email = binding.emailTv.text!!.toString()
            val pass = binding.passwordTv.text!!.toString()
            if (email.isNotEmpty() || pass.isNotEmpty()) {
                Utils.getInstance().signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            firebaseUser = FirebaseAuth.getInstance().currentUser!!
                            if (firebaseUser.isEmailVerified) {
                                Log.d(
                                    "EmailVerification",
                                    "Email Verified: ${Utils.getInstance().currentUser?.isEmailVerified}"
                                )


                                Utils.successToast(requireContext(), "Email verified")
                                startActivity(Intent(requireContext(), MainActivity::class.java))
                                Utils.successToast(requireContext(), "Login Succesfully")
                                requireActivity().finish()
                            } else {
                                Utils.normalDialog(
                                    requireContext(),
                                    "Verify your email by clicking on link send on your email "
                                )
                                Log.d(
                                    "EmailVerification",
                                    "Email Verified: ${Utils.getInstance().currentUser?.isEmailVerified}"
                                )

                                Utils.makeToast(
                                    requireContext(),
                                    "Verify your email by clicking on link send on your email"
                                )


                            }


                        } else {
                            Utils.makeToast(requireContext(), "Login Failed")
                        }

                    }

            }


        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {

    }
}