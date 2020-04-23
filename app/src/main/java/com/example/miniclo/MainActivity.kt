package com.example.miniclo

import android.content.Intent
import android.net.Uri
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
import kotlinx.android.synthetic.main.activity_main.category
import kotlinx.android.synthetic.main.activity_main.date_added
import kotlinx.android.synthetic.main.activity_main.tags
import java.io.File

class MainActivity : AppCompatActivity() {
    //        create an instance of Firebase Database
    private var mDatabase : FirebaseDatabase = FirebaseDatabase.getInstance()
    //        reference to the root node
    private lateinit var itemReference : DatabaseReference

    private lateinit var itemListener: ValueEventListener

    private lateinit var storage: FirebaseStorage
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        itemReference = Firebase.database.reference.child("/items")
    }

    public override fun onStart() {
        super.onStart()

        val itemListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val item: Item? = dataSnapshot.child("1").getValue<Item>()
                category.text = item?.category
                date_added.text = item?.date_added
                var t = ""
                for (tag in item?.tags!!) {
                    t += tag + ", "
                }
                tags.text = t
                laundry_status.text = item?.laundry_status.toString()
                worn_frequency.text = item?.worn_frequency.toString()

            }
        }

        itemReference.addValueEventListener(itemListener)
        this.itemListener = itemListener
        val tags_lst = listOf<String>("Red", "White", "Short-sleeve")
        val new_item : Item = Item("Dress", "04-21-2020", "img3.jpg", false, 10, tags_lst)
        itemReference.child("3").setValue(new_item)
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
