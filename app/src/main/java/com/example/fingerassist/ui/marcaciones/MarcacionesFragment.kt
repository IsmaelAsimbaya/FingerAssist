package com.example.fingerassist.ui.marcaciones

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fingerassist.CallBack
import com.example.fingerassist.Utils.FingerAssist
import com.example.fingerassist.Utils.FingerAssist.Companion.sp
import com.example.fingerassist.Utils.FingerAssist.Companion.db
import com.example.fingerassist.Utils.User
import com.example.fingerassist.databinding.FragmentMarcacionesBinding
import com.example.fingerassist.ui.adapter.UserItemAdapter2
import com.google.firebase.firestore.*

class MarcacionesFragment : Fragment() {

    private var _binding: FragmentMarcacionesBinding? = null

    private val binding get() = _binding!!

    private lateinit var userArrayList: ArrayList<User>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarcacionesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        userArrayList = ArrayList()

        eventChangeListener(object : CallBack {
            override fun onCallBack(value: Boolean) {
                if (value) {
                    initRecycler(userArrayList)
                }
            }
        })

        return root
    }

    private fun initRecycler(myList: ArrayList<User>) {
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = UserItemAdapter2(myList)
        recyclerView.adapter = adapter
    }

    private fun eventChangeListener(myCallback: CallBack) {
        var aux: Boolean
        db.collection("users").document(sp.getName()).collection("marcaciones").get()
            .addOnSuccessListener {
                for (documento in it) {
                    userArrayList.add(documento.toObject(User::class.java))
                }
                aux = true
                if (aux) {
                    myCallback.onCallBack(true)
                } else {
                    myCallback.onCallBack(false)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}