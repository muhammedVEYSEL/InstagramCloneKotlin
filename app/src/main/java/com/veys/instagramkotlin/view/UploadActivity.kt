package com.veys.instagramkotlin.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.veys.instagramkotlin.databinding.ActivityUploadBinding
import java.util.UUID

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedImage : Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore : FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        fireStore = Firebase.firestore
        storage = Firebase.storage


        registerLauncher()

    }

    fun upload(view: View){
        // tek bir isimle upload yaptığımız zaman firebase ilk upload olanın üstüne override yapar bu yüzden bu yöntemle her görsele random id atanır
        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"

        val reference = storage.reference // bu bize firebase sitesindeki boş storage alanını verir
        val imageReference = reference.child("Images/${imageName}") // bu storage ye Images onu altına da image.jpg diye klasör oluşturur
        if (selectedImage!=null){
            imageReference.putFile(selectedImage!!).addOnSuccessListener { // storage kayıt
                val uploadImageReference = storage.reference.child("Images/${imageName}")
                uploadImageReference.downloadUrl.addOnSuccessListener {
                    val downloadUri = it.toString()

                    val postMap = HashMap<String,Any>()
                    postMap.put("imagesUri",downloadUri)
                    postMap.put("userEmail",auth.currentUser!!.email!!)
                    postMap.put("comment",binding.editTextComment.text.toString())
                    postMap.put("date",Timestamp.now())

                    fireStore.collection("Post").add(postMap).addOnSuccessListener {// firestore kayıt
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this@UploadActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                    }


                }

            }.addOnFailureListener { it:Exception ->
                Toast.makeText(this@UploadActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }


    }
    fun selectimage(view:View){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(this@UploadActivity,Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this@UploadActivity,Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(view,"Permission needed to gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission!",View.OnClickListener {

                        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }).show()
                }else{
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }

            }else{
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }else{
            if (ContextCompat.checkSelfPermission(this@UploadActivity,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.shouldShowRequestPermissionRationale(this@UploadActivity,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Permission needed to gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission!",View.OnClickListener {

                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
                }else{
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }

            }else{
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }


    }
    fun registerLauncher(){
        activityResultLauncher= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->

            if (result.resultCode == RESULT_OK){
                val resultdataIntent = result.data
                if (resultdataIntent != null){
                    selectedImage = resultdataIntent.data
                    selectedImage?.let {
                        binding.imageView.setImageURI(selectedImage)
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){result ->
            if(result){

                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }else{
                Toast.makeText(this@UploadActivity,"I need permission to gallery",Toast.LENGTH_LONG).show()
            }

        }

    }





}