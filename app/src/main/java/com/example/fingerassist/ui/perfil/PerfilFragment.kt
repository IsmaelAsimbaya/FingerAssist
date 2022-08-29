package com.example.fingerassist.ui.perfil

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fingerassist.CallBack
import com.example.fingerassist.R
import com.example.fingerassist.Utils.FingerAssist
import com.example.fingerassist.Utils.FingerAssist.Companion.sp
import com.example.fingerassist.Utils.User
import com.example.fingerassist.databinding.FragmentPerfilBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.*
import com.squareup.picasso.Picasso

class PerfilFragment : Fragment() {

    private var _binding: FragmentPerfilBinding? = null

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
        val perfilViewModel =
            ViewModelProvider(this).get(PerfilViewModel::class.java)

        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        val root: View = binding.root

        cargaDatosUsuario(sp.getName())

        return root
    }

    private fun cargaDatosUsuario(email: String?) {
        //val hView: View = navView.getHeaderView(0)
        //val correo: TextView = hView.findViewById(R.id.userName)
        //correo.setText(email)
        db.collection("users").document(email!!).get().addOnSuccessListener {

            val img: ImageView = binding.imageUser
            Picasso.get().load(it.get("img") as String?).resize(400, 400)
                .centerCrop().into(img)

            val userName: TextView = binding.textUserName
            userName.setText(it.get("name") as String?)

            val userCi: TextView = binding.textCI
            userCi.setText(it.get("ci") as String?)

            val userCorreo: TextView = binding.textCorreo
            userCorreo.setText(sp.getName())

            val userTlfMov: TextView = binding.textTlfMovil
            userTlfMov.setText(it.get("movil") as String?)

            val userTlfFij: TextView = binding.textTlfFijo
            userTlfFij.setText(it.get("fijo") as String?)

            val userDirect: TextView = binding.textDireccion
            userDirect.setText(it.get("direccion") as String?)

        }

    }

    /*
    private fun EventChangeListener(myCallback: CallBack): List<User>{
        var aux: Boolean

        db.collection("users").addSnapshotListener(object :
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

     */

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}