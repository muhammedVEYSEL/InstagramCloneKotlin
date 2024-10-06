package com.veys.instagramkotlin.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.veys.instagramkotlin.R
import com.veys.instagramkotlin.adapter.RecycleAdapter
import com.veys.instagramkotlin.databinding.ActivityFeedBinding
import com.veys.instagramkotlin.model.Post

class FeedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var fireBase : FirebaseFirestore
    private lateinit var postArrayList: ArrayList<Post>
    private lateinit var postAdapter : RecycleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        fireBase = Firebase.firestore

        postArrayList = ArrayList<Post>()

        getData() // bundan sonra ekran değişecektir bu yüzden go to 63

        postAdapter = RecycleAdapter(postArrayList)
        binding.recycleview.layoutManager = LinearLayoutManager(this)
        binding.recycleview.adapter = postAdapter

    }

    fun getData(){
        // order by sıralama yapması için , orderby yerine . koyduktan sonra where şeklinde gelen seçenekler ile filtre işlemleri yapılabilir
        fireBase.collection("Post").orderBy("date",Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if (error!=null){// hata varsa
                Toast.makeText(this@FeedActivity,error.localizedMessage,Toast.LENGTH_LONG).show()
            }else{
                if (value!=null){// hata yoksa ve value null değilse
                    if (!value.isEmpty){// value boş değilse (kayıt edilmiş veri varsa)
                        val documents = value.documents
                        postArrayList.clear()

                        for(document in documents){
                            val userEmail = document.get("userEmail") as String
                            val imagesUri = document.get("imagesUri") as String
                            val comment = document.get("comment") as String

                            val post = Post(userEmail,comment,imagesUri)
                            postArrayList.add(post)
                        }
                        postAdapter.notifyDataSetChanged() // veriler çekildikten sonra olan değişiklikleri göstermek için yazılır
                    }
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInFilter = menuInflater
        menuInFilter.inflate(R.menu.insta_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.addPost){
            val intent = Intent(this@FeedActivity, UploadActivity::class.java)
            startActivity(intent)
        }
        else if(item.itemId == R.id.signout){
            auth.signOut()
            val intent = Intent(this@FeedActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}