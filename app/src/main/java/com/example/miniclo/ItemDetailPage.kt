package com.example.miniclo

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.text.Layout
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_item_detail_page.*


class ItemDetailPage : AppCompatActivity() {

    var itemReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("/items")
    private lateinit var itemsListener: ValueEventListener
    private lateinit var userListener: ValueEventListener
    private var mStorageRef : StorageReference = FirebaseStorage.getInstance().getReference("/images")
    private var userReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("/users")
    private var userUid : String = FirebaseAuth.getInstance().currentUser?.uid.toString()
    lateinit var item : Item

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

    @SuppressLint("ResourceType")
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
                val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialog)
                val title = SpannableString("Delete Item?")

                // alert dialog title align center
                title.setSpan(
                    AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                    0,
                    title.length,
                    0
                )
                // set message of alert dialog
                dialogBuilder.setTitle(title)
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
                // show alert dialog
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
                            userReference.child("$userUid/laundry_list/$item_key").setValue(null)
                        })
                    } else {
                        status_value.text = "Not in Laundry"
                        laundry_btn.text = "Add To Laundry"
                        laundry_btn.setOnClickListener(View.OnClickListener {
                            curr_item_ref.child("laundry_status").setValue(true)
                            curr_item_ref.child("worn_frequency").setValue(db_item!!.worn_frequency + 1)
                            userReference.child("$userUid/laundry_list/$item_key").setValue(true)
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