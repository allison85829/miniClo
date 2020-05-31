package com.example.miniclo

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_item_detail_page.*

class ItemDetailPage : AppCompatActivity() {

    var itemReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("/items")
    var userReference: DatabaseReference =
        FirebaseDatabase.getInstance().reference
        .child("/users/${ FirebaseAuth.getInstance().currentUser?.uid}")
    private lateinit var itemsListener: ValueEventListener
    private lateinit var userListener: ValueEventListener
    private var mStorageRef : StorageReference = FirebaseStorage.getInstance().getReference("/images")
    lateinit var item : Item
//    lateinit var del_btn : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail_page)

        item = intent.getParcelableExtra("item_obj")
        //item_tags_value.text = item.tags.toString()
        tag_1.text = item.category
        tag_2.text = item.tags[1]
        tag_3.text = item.tags[2]
        item_date_added_value.text = item.date_added
        Picasso.get().load(item.image).into(uploadImgPreview)

        setUpLaundryBtn(item.key)
        setUpActionBar()
        setUpUser()
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
                val dialogBuilder = AlertDialog.Builder(this)

                // set message of alert dialog
                dialogBuilder.setMessage("Are you sure you want to delete item?")
                    // if the dialog is cancelable
                    .setCancelable(false)
                    // positive button text and action
                    .setPositiveButton("Delete", DialogInterface.OnClickListener {
                            dialog, id ->
                        deleteItem()
                        dialog.cancel()
                        finish()
                    })
                    // negative button text and action
                    .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                            dialog, id -> dialog.cancel()
                    })

                // create dialog box
                val alert = dialogBuilder.create()
                // set title for alert dialog box
//                alert.setTitle("Delete Confirmation")
//                 show alert dialog
                alert.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun deleteItem() {
        var dbRef : DatabaseReference = FirebaseDatabase.getInstance().reference
        var update = HashMap<String, Any?>()
        update.put("/items/${this.item.key}", null)
        update.put("/users/${FirebaseAuth.getInstance().currentUser?.uid}/item_list/${this.item.key}", null)
        update.put("/users/${FirebaseAuth.getInstance().currentUser?.uid}/laundry_list/${this.item.key}", null)
        var imgRef : StorageReference = mStorageRef.child("${this.item.img_name}")
        imgRef.delete()
        dbRef.updateChildren(update)
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

    fun setUpUser() {
        this.userListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {}

            override fun onCancelled(databaseError: DatabaseError) {}
        }
        this.userReference.addValueEventListener(this.userListener)
    }

}