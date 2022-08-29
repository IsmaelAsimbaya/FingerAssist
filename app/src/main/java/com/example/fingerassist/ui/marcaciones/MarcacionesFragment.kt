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
import com.example.fingerassist.Utils.User
import com.example.fingerassist.databinding.FragmentMarcacionesBinding
import com.example.fingerassist.ui.adapter.UserItemAdapter2
import com.google.firebase.firestore.*

class MarcacionesFragment : Fragment() {

    private var _binding: FragmentMarcacionesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var userArrayList: ArrayList<User>
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val marcacionesViewModel =
            ViewModelProvider(this).get(MarcacionesViewModel::class.java)

        _binding = FragmentMarcacionesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        userArrayList = ArrayList()

        EventChangeListener(object : CallBack {
            override fun onCallBack(value: Boolean) {
                if (value){

                    initRecycler(binding.root,userArrayList)
                }
            }
        })

        return root
    }

    private fun initRecycler(itemView: View, myList: ArrayList<User>){
        val recyclerView = binding.recyclerView
        //val recyclerView = itemView.findViewById(R.id.recyclerView) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = UserItemAdapter2(myList)
        recyclerView.adapter = adapter
    }

    private fun EventChangeListener(myCallback: CallBack): List<User>{
        var aux: Boolean
        //var us: User = User("12","hora", "12:00", "13:00","2")
        //userArrayList = arrayListOf<User>(us)
        db.collection("users").document(FingerAssist.sp.getName()).collection("marcaciones").addSnapshotListener(object :
            EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null){
                    Log.e("Firestore Error---", error.message.toString())
                    return
                }
                for (dc : DocumentChange in value?.documentChanges!!){
                    if (dc.type == DocumentChange.Type.ADDED){
                        userArrayList.add(dc.document.toObject(User::class.java))
                    }
                }

                aux = true
                Log.d("list",userArrayList.toString())
                if (aux){
                    myCallback.onCallBack(true)
                }else{
                    myCallback.onCallBack(false)
                }
            }
        })
        return userArrayList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}