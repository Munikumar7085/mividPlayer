package com.example.mividplayer.fragments.details

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.mividplayer.R
import com.example.mividplayer.R.color.skin
import com.example.mividplayer.databinding.FragmentSignInBinding


class SignInFragment : Fragment() {
    private lateinit var binding:FragmentSignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_sign_in,container,false)
        binding.signInBtn.setOnClickListener{
            binding.signInBtn.setBackgroundResource(R.drawable.btn_sign_in)

            binding.signInBtn.setBackgroundResource(0)

        }
        binding.signInWithGoogle.setOnClickListener{

        }
        binding.signUp.setOnClickListener{
            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToSignUpFragment())
        }
        binding.forgotPassword.setOnClickListener{
            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToForgotPasswordFragment())
        }
        binding.signInSkip.setOnClickListener{
            findNavController().navigate(SignInFragmentDirections.actionSignInFragmentToMainViewFragment("0"))
        }
        return binding.root
    }


}