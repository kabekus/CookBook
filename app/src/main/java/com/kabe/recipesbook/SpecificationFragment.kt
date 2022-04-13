package com.kabe.recipesbook

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_specification.*
import java.io.ByteArrayOutputStream

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

        arguments?.let{
            var inComingInformation = SpecificationFragmentArgs.fromBundle(it).information
            if (inComingInformation.equals("menudengeldim")){
                cookNameText.setText("")
                cookMaterialsText.setText("")
                saveButton.visibility = View.VISIBLE

                val selectPicBackGround = BitmapFactory.decodeResource(context?.resources,R.drawable.picture)
                imageView.setImageBitmap(selectPicBackGround)
            }else{
                saveButton.visibility = View.INVISIBLE

                val selectId = SpecificationFragmentArgs.fromBundle(it).id

                context?.let{
                    try {
                        val db = it.openOrCreateDatabase("Foods",Context.MODE_PRIVATE,null)
                        val cursor = db.rawQuery("SELECT * FROM cooks WHERE id = ?", arrayOf(selectId.toString()))

                        val cookNameIndex = cursor.getColumnIndex("cookName")
                        val cookMaterialsIndex = cursor.getColumnIndex("cookMaterialsText")
                        val cookPicture = cursor.getColumnIndex("pictures")

                        while (cursor.moveToNext()){
                            cookNameText.setText(cursor.getString(cookNameIndex))
                            cookMaterialsText.setText(cursor.getString(cookMaterialsIndex))
                            val byteArray = cursor.getBlob(cookPicture)
                            val bitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                            imageView.setImageBitmap(bitmap)
                        }
                        cursor.close()
                    }catch (e : Exception){
                        e.printStackTrace()
                    }
                }

            }
        }
    }

    fun saveButton(view: View){
        val cookName = cookNameText.text.toString()
        val cookMaterialsText = cookMaterialsText.text.toString()

        if (selectBitmap != null){
            val smallBitmapCreated = smallBitmap(selectBitmap!!,300)

            val outputStream = ByteArrayOutputStream()
            smallBitmapCreated.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteArray = outputStream.toByteArray()

            try{
                    context?.let{
                        val database = it.openOrCreateDatabase("Foods",Context.MODE_PRIVATE,null)
                        database.execSQL("CREATE TABLE IF NOT EXISTS cooks(id INTEGER PRIMARY KEY, cookName VARCHAR,cookMaterialsText VARCHAR, pictures BLOB)")
                        val sqlString = "INSERT INTO cooks(cookName,cookMaterialsText,pictures) VALUES (?,?,?)"
                        val statement = database.compileStatement(sqlString)
                        statement.bindString(1,cookName)
                        statement.bindString(2,cookMaterialsText)
                        statement.bindBlob(3,byteArray)
                        statement.execute()
                    }


            }catch (e: Exception){

            }
            val action = SpecificationFragmentDirections.actionSpecificationFragmentToListFragment()
            Navigation.findNavController(view).navigate(action)
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
       if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null){
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