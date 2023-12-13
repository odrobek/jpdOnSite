/**
 * Olek Drobek
 * CSC 4911
 * Final Project - JPD OnSite
 * LoginFragment.kt
 */
package edu.msoe.drobeka.jpdonsite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import edu.msoe.drobeka.jpdonsite.databinding.FragmentLoginBinding

class LoginFragment: Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dictionary = mapOf("" to "", "username" to "password")

        binding.button.setOnClickListener {
            // check if username and password match, then continue to next fragment
            if(binding.usernameEditText.text.toString() in dictionary.keys
                && binding.passwordEditText.text.toString() == dictionary[binding.usernameEditText.text.toString()]) {
                findNavController().navigate(
                    LoginFragmentDirections.loginAction()
                )
            } else {
                Toast.makeText(requireContext(), R.string.bad_login, Toast.LENGTH_SHORT).show()
            }
        }
    }
}