package com.example.miniclo

import com.bumptech.glide.Glide
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.solver.widgets.Snapshot
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_main.*

//import kotlinx.android.synthetic.main.activity_main.category
//import kotlinx.android.synthetic.main.activity_main.date_added
//import kotlinx.android.synthetic.main.activity_main.tags

import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi

import androidx.core.os.postDelayed
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
//import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.AppBarConfiguration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class MainActivity : AppCompatActivity() {
    //        create an instance of Firebase Database
    private var mDatabase : FirebaseDatabase = FirebaseDatabase.getInstance()
    //        reference to the root node
    private lateinit var itemReference : DatabaseReference

    private lateinit var itemListener: ValueEventListener

    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth
    val user : FirebaseUser? = FirebaseAuth.getInstance().getCurrentUser()

    lateinit var tags : TextView
    lateinit var date_added : TextView
    lateinit var worn_frequency : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        itemReference = Firebase.database.reference.child("/items")

        if(auth.currentUser == null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            Toast.makeText(this, "Already logged in", Toast.LENGTH_LONG).show()
        }
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.mainNavFragment)

        // Set up ActionBar
        //setSupportActionBar(toolbar)
        //NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)

        //var appBarConfiguration = AppBarConfiguration(setOf(R.id.bottomNavFragmentCloset, R.id.bottomNavFragmentLaundry, R.id.bottomNavFragmentStats, R.id.bottomNavFragmentAccount))
        //setupActionBarWithNavController(navController, appBarConfiguration)

        // Set up navigation menu
        navigationView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(
            Navigation.findNavController(this, R.id.mainNavFragment), drawerLayout)
    }

    public override fun onStart() {
        super.onStart()

        val itemListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val item: Item? = dataSnapshot.child("-M67mYjnX315Q4abm4Xj").getValue<Item>()
                if (item != null) {
                    val img : ImageView = findViewById<ImageView>(R.id.item_img)
                    tags = findViewById(R.id.tags_text)
                    var tg = ""
                    for (t in item.tags) {
                        tg += ", " + t
                    }
                    tags.setText("Tags: " + tg)

                    date_added = findViewById(R.id.date_added_text)
                    date_added.setText(item.date_added)

                    worn_frequency = findViewById(R.id.worn_frequency)
                    worn_frequency.setText(item.worn_frequency.toString())
                    Glide.with(this@MainActivity)
                        .load(item.image)
                        .into(img)
                }
            }
        }

        itemReference = Firebase.database.reference.child("/items")
        itemReference.addValueEventListener(itemListener)
        this.itemListener = itemListener
    }

    public override fun onStop() {
        super.onStop()
        itemReference.removeEventListener(this.itemListener)
    }

    fun addItem(view: View) {
        val intent = Intent(this, AddItem::class.java)
        startActivity(intent)
    }
}
