package com.example.miniclo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_item_detail_page.*

class ItemDetailPage : AppCompatActivity() {

    var itemReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("/items")
    lateinit var item : Item
//    lateinit var del_btn : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail_page)

        item = intent.getParcelableExtra("item_obj")
        item_tags_value.text = item.tags.toString()
        item_date_added_value.text = item.date_added
        Picasso.get().load(item.image).into(item_img)

        setUpLaundryBtn(item.key)
        setUpActionBar()
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.item_detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_edit -> {
                Toast.makeText(
                    this,
                    "Action Edit Clicked",
                    Toast.LENGTH_SHORT
                ).show()
                true
            }
            R.id.action_delete -> {
                val popUpClass = PopUpClass()
                popUpClass.showPopupWindow(findViewById(R.id.item_card), this.item.key)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun setUpActionBar() {
        val actionbar = supportActionBar
        actionbar!!.title = "Closet Item"
        actionbar.setDisplayHomeAsUpEnabled(true)
    }

    fun setUpLaundryBtn(item_key : String) {
        var curr_item_ref = itemReference.child(item_key)!!

        var itemListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var db_item = dataSnapshot.getValue(Item::class.java)
                if (db_item != null) {
                    item_times_worn_value.text= Integer.toString(db_item!!.worn_frequency)

                    if (db_item!!.laundry_status) {
                        status_value.text = "In Laundry"
                        laundry_btn.text = "Remove From Laundry"
                        laundry_btn.setOnClickListener(View.OnClickListener {
                            curr_item_ref.child("laundry_status").setValue(false)
                        })
                    } else {
                        status_value.text = "Not in Laundry"
                        laundry_btn.text = "Add To Laundry"
                        laundry_btn.setOnClickListener(View.OnClickListener {
                            curr_item_ref.child("laundry_status").setValue(true)
                            curr_item_ref.child("worn_frequency").setValue(db_item!!.worn_frequency + 1)
                        })
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        curr_item_ref.addValueEventListener(itemListener as ValueEventListener)
    }
}