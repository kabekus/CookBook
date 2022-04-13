package com.kabe.recipesbook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_row.view.*

class ListRecyclerAdapter(val cookList : ArrayList<String> , val idList : ArrayList<Int>) : RecyclerView.Adapter<ListRecyclerAdapter.CookHolder>(){
    class CookHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CookHolder {
        val inflater = LayoutInflater.from((parent.context))
        val view = inflater.inflate(R.layout.recycler_row,parent,false)
        return CookHolder(view)
    }

    override fun onBindViewHolder(holder: CookHolder, position: Int) {
        holder.itemView.rowText.text=cookList[position]
        holder.itemView.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToSpecificationFragment("recyclerdangeldim",idList[position])
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return cookList.size
    }
}