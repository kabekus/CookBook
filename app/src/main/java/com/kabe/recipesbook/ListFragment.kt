package com.kabe.recipesbook

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list.*
import java.util.*
import kotlin.collections.ArrayList

class ListFragment : Fragment() {

    var cookNameList = ArrayList<String>()
    var cookIdList = ArrayList<Int>()
    private lateinit var listAdapter : ListRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listAdapter = ListRecyclerAdapter(cookNameList,cookIdList)
        recyclerView.layoutManager=LinearLayoutManager(context)
        recyclerView.adapter=listAdapter

        sqLiteData()
    }

    fun sqLiteData(){
        try {
            activity?.let{
                val database = it.openOrCreateDatabase("Foods", Context.MODE_PRIVATE,null)
                val cursor = database.rawQuery("SELECT * FROM cooks",null)
                val cookNameIndex = cursor.getColumnIndex("cookName")
                val cookIdIndex = cursor.getColumnIndex("id")

                cookNameList.clear()
                cookIdList.clear()

                while (cursor.moveToNext()){
                    cookNameList.add(cursor.getString(cookNameIndex))
                    cookIdList.add(cursor.getInt(cookIdIndex))
                }
                listAdapter.notifyDataSetChanged() // Yeni veri gelince listeye ekleme

                cursor.close()
            }
        }catch (e: Exception){

        }
    }
}