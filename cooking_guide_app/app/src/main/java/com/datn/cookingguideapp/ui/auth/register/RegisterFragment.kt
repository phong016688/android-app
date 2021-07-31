package com.datn.cookingguideapp.ui.auth.register

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.datn.cookingguideapp.R
import com.datn.cookingguideapp.databinding.FragmentRegisterBinding
import com.datn.cookingguideapp.ui.home.HomeActivity
import com.datn.cookingguideapp.utils.viewBindings
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class RegisterFragment : Fragment(R.layout.fragment_register) {
    private val viewBinding by viewBindings(FragmentRegisterBinding::bind)
    private val viewModel by viewModel<RegisterViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.liveData.observe(viewLifecycleOwner, {
            Toast.makeText(requireContext(), "Đăng kí thành công", Toast.LENGTH_SHORT).show()
            startActivity(HomeActivity.makeActivity(requireContext()))
            activity?.finish()
        })

        viewModel.liveDataErr.observe(viewLifecycleOwner, {
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
        })

        viewBinding.registerButton.setOnClickListener { register() }

        viewBinding.signNavigateButton.setOnClickListener { findNavController().popBackStack() }
    }

    private fun register() {
        val email = viewBinding.emailRegisterEditText.text?.toString() ?: ""
        val fullName = viewBinding.fullNameRegisterEditText.text?.toString() ?: ""
        val password = viewBinding.passwordRegisterEditText.text?.toString() ?: ""
        val address = viewBinding.addressEditText.text?.toString() ?: ""
        val avatar = "https://st.quantrimang.com/photos/image/072015/22/avatar.jpg"
        val simpleFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
        val date = simpleFormat.format(Date()).toString()
    }
}
