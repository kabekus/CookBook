package com.kabe.recipesbook

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_specification.*

class SpecificationFragment : Fragment() {
    var selectpic : Uri? = null
    var selectBitmap : Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_specification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        saveButton.setOnClickListener {
            saveButton(it)
        }

        imageView.setOnClickListener {
            selectPictures(it)
        }
    }

    fun saveButton(view: View){
        val cookName = cookNameText.text.toString()
        val cookMaterialsText = cookMaterialsText.text.toString()

        if (selectBitmap != null){
            val smallBitmapCreated = smallBitmap(selectBitmap!!,300)
            
        }

        activity?.let {
            Toast.makeText(it.applicationContext,"Kaydedildi",Toast.LENGTH_LONG).show() //Toast Message
        }


    }
    fun selectPictures(view: View){
        activity?.let {
            if(ContextCompat.checkSelfPermission(it.applicationContext,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){ //Kullanıcının galerisine erişim izni
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
            }else{
                val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray ) {

        if (requestCode == 1){
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
       if (requestCode == 2 && requestCode == Activity.RESULT_OK && data != null){
           selectpic = data.data
           try {
               context?.let {
                   if (selectpic != null){
                       if (Build.VERSION.SDK_INT >= 28){
                         val source = ImageDecoder.createSource(it.contentResolver,selectpic!!)
                           selectBitmap = ImageDecoder.decodeBitmap(source)
                           imageView.setImageBitmap(selectBitmap)
                       }else{
                           selectBitmap = MediaStore.Images.Media.getBitmap(it.contentResolver,selectpic)
                           imageView.setImageBitmap(selectBitmap)
                       }
                   }
               }
           }catch (e : Exception){
               e.printStackTrace()
           }
       }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun smallBitmap(userSelectBitmap : Bitmap , maxDimension : Int) : Bitmap{
        var width = userSelectBitmap.width
        var height = userSelectBitmap.height

        val bitmapRatio : Double = width.toDouble() / height.toDouble()

        if (bitmapRatio > 1){
            //Görsel Yataysa
            width = maxDimension
            val shortenedHeight = width / bitmapRatio
            height = shortenedHeight.toInt()
        }else{
            //Görsel Dikeyse
            height = maxDimension
            val shortenedWidth = height * bitmapRatio
            width = shortenedWidth.toInt()
        }
        return Bitmap.createScaledBitmap(userSelectBitmap,width,height,true)
    }
}