package com.veys.instagramkotlin.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.veys.instagramkotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private var email : String = ""
    private var password : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth=Firebase.auth


        val currentUser = auth.currentUser // aktif olarak giriş yapmış bir kullanıcı var mı onu elde ederiz
        if (currentUser != null){
            val intent = Intent(this@MainActivity, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun signin(view: View){

        email = binding.editTextEmail.text.toString()
        password = binding.editTextPassword.text.toString()

        if (email.equals("")||password.equals("")){
            Toast.makeText(this@MainActivity,"Please enter information!",Toast.LENGTH_LONG).show()
        }else{
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                val intent = Intent(this@MainActivity, FeedActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener { it:Exception ->
                Toast.makeText(this@MainActivity,it.localizedMessage,Toast.LENGTH_LONG).show()

            }
        }
    }
    fun signup(view:View){
        email = binding.editTextEmail.text.toString()
        password = binding.editTextPassword.text.toString()

        if(email.equals("")||password.equals("")){
            Toast.makeText(this@MainActivity,"Please enter information",Toast.LENGTH_LONG).show()
        }else{ // fire base ile olan işlemler arka planda yapılması lazım o yüzden .addsucces vb kısımlar kullanırız
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
                val intent = Intent(this@MainActivity, FeedActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener { it:Exception ->
                Toast.makeText(this@MainActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }

    }
}