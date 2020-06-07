package com.example.miniclo

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_stats_to_donate.*

/**
 * A simple [Fragment] subclass.
 */
class FragmentStatsToDonate : Fragment() {

    private var mDatabase : FirebaseDatabase = FirebaseDatabase.getInstance()
    private var itemsReference : DatabaseReference = mDatabase.reference.child("/items")
    private var userReference : DatabaseReference = mDatabase.reference.child("/users")
    private var mStorageRef : StorageReference = FirebaseStorage.getInstance().getReference("/images")
    private lateinit var itemsListener: ValueEventListener
    private var userUid : String = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private lateinit var items_to_donate : ArrayList<Item>
    private var NUM_DONATE = 2

    override fun onStart() {
        super.onStart()

        val itemsListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val items = dataSnapshot!!.children
                val itemsArr : ArrayList<Item> = ArrayList<Item>()
                items.forEach {
                    val item: Item? = it.getValue<Item>()
                    if (item != null) {
                        item.key = it.key!!
                        itemsArr.add(item)
                    }
                }
                itemsArr.sortBy{ it.worn_frequency }
                items_to_donate = itemsArr.take(NUM_DONATE).toList() as ArrayList<Item>
                setupRecyclerView(items_to_donate)
            }
        }

        itemsReference.orderByChild("user").equalTo(userUid).addListenerForSingleValueEvent(itemsListener)
        this.itemsListener = itemsListener
    }

    override fun onStop() {
        super.onStop()
        itemsReference.removeEventListener(itemsListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).setSupportActionBar(toolbar_to_donate)

        // Adds back button to get back to map
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        /*
        back_most_worn.setOnClickListener{
            (activity as AppCompatActivity).finish()
        }

         */

        return inflater.inflate(R.layout.fragment_stats_to_donate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up back button
        toolbar_to_donate.setNavigationIcon(R.drawable.ic_back) // need to set the icon here to have a navigation icon. You can simple create an vector image by "Vector Asset" and using here
        toolbar_to_donate.setNavigationOnClickListener {
            // do something when click navigation
            //(activity as AppCompatActivity).finish()
            getFragmentManager()?.popBackStackImmediate()
        }

        donateItemsBtn.setOnClickListener{
            val dialogBuilder = AlertDialog.Builder(activity)
            dialogBuilder.setMessage("Are you sure want to donate these items? This will permanently remove 10 items from your closet.")
                .setCancelable(false)
                .setPositiveButton("Donate", DialogInterface.OnClickListener {
                        dialog, id ->
                        run {
                            dialog.cancel()
                            donateItems()
                            Toast.makeText(context, "Successfully donated items", Toast.LENGTH_SHORT).show()
                            getFragmentManager()?.popBackStackImmediate()
                    }

                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                        dialog, id -> dialog.cancel()
                })

            val alert = dialogBuilder.create()
            alert.setTitle("Donate Items")
            alert.show()
        }
    }

    fun donateItems() {
        for (item in items_to_donate) {
            itemsReference.child(item.key).setValue(null)
            userReference.child(userUid).child("item_list").child(item.key).setValue(null)
            userReference.child(userUid).child("laundry_list").child(item.key).setValue(null)
            var imgRef : StorageReference = mStorageRef.child("${item.img_name}")
            imgRef.delete()
        }

        /*
        val item_count_ref: DatabaseReference = userReference!!.child(userUid).child("total_item")
        item_count_ref.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val currentValue = mutableData.getValue(Int::class.java)
                if (currentValue == null) {
                    mutableData.value = 0
                } else {
                    mutableData.value = currentValue - items_to_donate.size
                }
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                dataSnapshot: DataSnapshot?
            ) {
                println("Transaction completed")
            }
         */
    }

    fun setupRecyclerView(itemArr: ArrayList<Item>) {
        donateRecView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = ItemAdapter(itemArr)
        }
    }
}