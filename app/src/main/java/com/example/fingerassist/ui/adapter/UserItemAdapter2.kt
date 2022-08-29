package com.example.fingerassist.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fingerassist.R
import com.example.fingerassist.Utils.User
import com.example.fingerassist.databinding.ViewDatosBinding

class UserItemAdapter2 (
    private val itemListUser: ArrayList<User>) :
    RecyclerView.Adapter<UserItemAdapter2.UserItemViewHolder>() {

    class UserItemViewHolder(item: View): RecyclerView.ViewHolder(item){

        private var binding: ViewDatosBinding = ViewDatosBinding.bind(item)

        fun render(itemUser: User){
            binding.textViewDia.text = itemUser.dia.toString()
            binding.textViewHorario.text = "Horario Administrativo: "+itemUser.horario.toString()
            binding.textViewEntrada.text = itemUser.entrada.toString()
            binding.textViewSalida.text = itemUser.salida.toString()
            binding.textViewAtraso.text = itemUser.atraso.toString()

            binding.textViewNameDia.text = itemUser.name.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserItemViewHolder {
        val infrater = LayoutInflater.from(parent.context)
        return UserItemViewHolder(infrater.inflate(R.layout.view_datos, parent,false))
    }

    override fun onBindViewHolder(holder: UserItemViewHolder, position: Int) {
        holder.render(itemListUser[position])
    }

    override fun getItemCount(): Int {
        return itemListUser.size
    }

}