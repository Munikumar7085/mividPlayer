package com.example.mividplayer.fragments.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.mividplayer.R
import com.example.mividplayer.databinding.FragmentSignUpBinding


class SignUpFragment : Fragment() {
    private lateinit var binding:FragmentSignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_sign_up,container,false)
        binding.signUpBtn.setOnClickListener{
            findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToMainViewFragment("0"))
        }
        binding.backFromSignUp.setOnClickListener{
            findNavController().navigate(SignUpFragmentDirections.actionSignUpFragmentToSignInFragment())
        }
        return binding.root
    }


}