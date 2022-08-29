package com.example.fingerassist.ui.logout

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fingerassist.LoginActivity
import com.example.fingerassist.Utils.FingerAssist.Companion.sp
import com.example.fingerassist.databinding.FragmentCreditsBinding

class LogoutFragment : Fragment() {

    private var _binding: FragmentCreditsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCreditsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        sp.saveRemember(false)
        showLogin()

        return root
    }

    private fun showLogin( ) {
        val mainIntent = Intent(requireContext(), LoginActivity::class.java)
        /*sp.saveRemember(false)
        sp.saveUser("")
        sp.saveLatLng(setOf("0.0","0.0"))
        sp.saveAxis1(setOf("0.0","0.0"))
        sp.saveAxis2(setOf("0.0","0.0"))
        sp.saveAxis3(setOf("0.0","0.0"))
        sp.saveAxis4(setOf("0.0","0.0"))*/

        sp.getSharedPreference().edit().clear().apply()

        startActivity(mainIntent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}