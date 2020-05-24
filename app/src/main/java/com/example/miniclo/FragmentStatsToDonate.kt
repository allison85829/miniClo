package com.example.miniclo

import android.os.Bundle
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
import kotlinx.android.synthetic.main.fragment_stats_to_donate.*

/**
 * A simple [Fragment] subclass.
 */
class FragmentStatsToDonate : Fragment() {

    private var mDatabase : FirebaseDatabase = FirebaseDatabase.getInstance()
    private var itemsReference : DatabaseReference = mDatabase.reference.child("/items");
    private lateinit var itemsListener: ValueEventListener
    private var user : String = FirebaseAuth.getInstance().currentUser?.uid.toString()

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
                setupRecyclerView(itemsArr.take(10) as ArrayList<Item>)
            }
        }

        itemsReference.orderByChild("user").equalTo(user).addListenerForSingleValueEvent(itemsListener)
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
        toolbar_to_donate.setNavigationIcon(R.drawable.ic_home_black_24dp) // need to set the icon here to have a navigation icon. You can simple create an vector image by "Vector Asset" and using here
        toolbar_to_donate.setNavigationOnClickListener {
            // do something when click navigation
            //(activity as AppCompatActivity).finish()
            getFragmentManager()?.popBackStackImmediate()
        }

        donateItemsBtn.setOnClickListener{
            Toast.makeText(context, "Donate Items is clicked", Toast.LENGTH_SHORT).show()
        }
    }

    fun setupRecyclerView(itemArr: ArrayList<Item>) {
        donateRecView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = ItemAdapter(itemArr)
        }
    }
}
