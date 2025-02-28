package com.example.habittacker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.navigation.fragment.findNavController
import com.example.habittacker.R
import com.example.habittacker.Utils
import com.example.habittacker.databinding.FragmentSettingBinding
import kotlinx.coroutines.flow.MutableStateFlow


class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding
    var isremindtowalk= MutableStateFlow(false)
    var isremindtomediate= MutableStateFlow(false)
    var isreminddrinkwater= MutableStateFlow(false)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(layoutInflater, container, false)
        val sharedPreferences= requireActivity().getSharedPreferences("Setting", MODE_PRIVATE)
        isremindtomediate.value=sharedPreferences.getBoolean("ismeditate",false)
        isremindtowalk.value=sharedPreferences.getBoolean("isWalk",false)
        isreminddrinkwater.value=sharedPreferences.getBoolean("isdrink",false)

        binding.walkSwitch.isChecked = isremindtowalk.value
        binding.waterSwitch.isChecked = isreminddrinkwater.value
        binding.meditateSwitch.isChecked = isremindtomediate.value
        binding.aacountTv.setOnClickListener{
           findNavController().navigate(R.id.action_settingFragment_to_accountFragment)
        }




        val sharedEditor= requireActivity().getSharedPreferences("Setting", MODE_PRIVATE).edit()
        binding.walkSwitch.setOnCheckedChangeListener { _, isCheck ->
            if (isCheck) {
                Utils.successToast(requireContext(), "Okay!,We will remind you to walk ")
                isremindtowalk.value=true
                sharedEditor.putBoolean("isWalk",isremindtowalk.value).apply()
                binding.walkSwitch.isChecked= true
            } else {
                Utils.normalDialog(requireContext(), "We will not remind you to walk")
                isremindtowalk.value=false
                sharedEditor.putBoolean("isWalk",isremindtowalk.value).apply()
                binding.walkSwitch.isChecked= false
            }

        }


        binding.waterSwitch.setOnCheckedChangeListener { _, isCheck ->
            if (isCheck) {
                Utils.successToast(requireContext(), "Okay!,We will remind you to drink water")
                isreminddrinkwater.value=true
                sharedEditor.putBoolean("isdrink",isreminddrinkwater.value).apply()
                binding.waterSwitch.isChecked=true


            } else {
                Utils.normalDialog(requireContext(), "We will not remind you to drink water")
                isreminddrinkwater.value=false
                sharedEditor.putBoolean("isdrink",isreminddrinkwater.value).apply()
                binding.waterSwitch.isChecked=false
            }

        }
        binding.meditateSwitch.setOnCheckedChangeListener { _, isCheck ->
            if (isCheck) {
                Utils.successToast(requireContext(), "Okay!,We will remind you to meditate")
                isremindtomediate.value=true
                sharedEditor.putBoolean("ismeditate",isremindtomediate.value).apply()
                binding.meditateSwitch.isChecked = true



            } else {
                isremindtomediate.value=false
                Utils.normalDialog(requireContext(), "We will not remind you to meditate")
                sharedEditor.putBoolean("ismeditate",isremindtomediate.value).apply()
                binding.meditateSwitch.isChecked = false

            }


        }




        sharedEditor.apply()
        return binding.root
    }


}