package com.example.miniclo


//import android.R
import android.content.Intent
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_bottom_nav_fragment_stats.*


/**
 * A simple [Fragment] subclass.
 */
class BottomNavFragmentStats : androidx.fragment.app.Fragment() {
    lateinit var userReference: DatabaseReference
    lateinit var userListener: ValueEventListener
    lateinit var user: FirebaseUser
    var total_item_count = 0
    val laundry_count = 0
    //lateinit var item_count_text: TextView
    //private lateinit var listView : ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).setSupportActionBar(toolbar_stats)
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_nav_fragment_stats, container, false)
    }

    override fun onStart() {
        super.onStart()

        val userListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //statListItems[0] = "Total Item Count: " + dataSnapshot.childrenCount.toString()
                // move the code for attach the adapter into the function
                // and call it after all getting the total item count and the laundry count
//                attachAdapter()

                if (view != null) {
                    item_count?.text = dataSnapshot.childrenCount.toString()
                }

                total_item_count = dataSnapshot.childrenCount.toInt()
                attachAdapter()
            }
        }
        user = FirebaseAuth.getInstance().currentUser!!
        userReference = Firebase.database.reference.child("/users/${user.uid}/item_list")
//            .child(user.uid.toString())
//            .child("item_list")
//        userReference = Firebase.database.reference.child("/users").child(user.uid.toString())
//            .child("item_list")
        userReference.addValueEventListener(userListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recc_donations_btn.setOnClickListener {
            // Create new fragment and transaction
            val newFragment: Fragment = FragmentStatsToDonate()
            // consider using Java coding conventions (upper first char class names!!!)
            // consider using Java coding conventions (upper first char class names!!!)
            val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack
            transaction.add((getView()!!.parent as ViewGroup).id, newFragment)
            transaction.addToBackStack(null)

            // Commit the transaction
            transaction.commit()
        }
    }

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //statListItems[0] = "Total Item Count: " + dataSnapshot.childrenCount.toString()
                // move the code for attach the adapter into the function
                // and call it after all getting the total item count and the laundry count
//                attachAdapter()

                if (view != null) {
                    item_count!!.text = dataSnapshot.childrenCount.toString()
                }

                total_item_count = dataSnapshot.childrenCount.toInt()

                attachAdapter()
            }
        }
        user = FirebaseAuth.getInstance().currentUser!!
        userReference = Firebase.database.reference
            .child("/users/${user.uid}/item_list")
//            .child(user.uid.toString())
//            .child("item_list")
//        userReference = Firebase.database.reference.child("/users").child(user.uid.toString())
//            .child("item_list")
        userReference.addValueEventListener(userListener)

        recc_donations_btn.setOnClickListener {
            // Create new fragment and transaction
            val newFragment: Fragment = FragmentStatsToDonate()
            // consider using Java coding conventions (upper first char class names!!!)
            // consider using Java coding conventions (upper first char class names!!!)
            val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack
            transaction.add((getView()!!.parent as ViewGroup).id, newFragment)
            transaction.addToBackStack(null)

            // Commit the transaction
            transaction.commit()
        }
    }

     */

    fun attachAdapter(/*item_count : Int, laundry_count : Int*/) {

        if (activity != null) {
            val statListItems = arrayOf(
                "Total Item Count: $total_item_count",
                "Total Items In Laundry: $laundry_count",
                "Most Worn Items",
                "Least Worn Items"
            )
            var adapter = ArrayAdapter(
                activity,
                R.layout.stat_list_row,
                R.id.stat_row_text_view/*android.R.layout.simple_list_item_1*/,
                statListItems
            )
            stats_list_view.adapter = adapter

            stats_list_view.setOnItemClickListener(object : AdapterView.OnItemClickListener {
                override fun onItemClick(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                    when (i) {
                        0 -> {}//stats_list_view.getChildAt(0).setEnabled(false)
                        1 -> {}//stats_list_view.getChildAt(1).setEnabled(false)
                        2 -> {
                            val newFragment: Fragment = FragmentStatsMostWorn()
                            val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

                            transaction.add((getView()!!.parent as ViewGroup).id, newFragment)
                            transaction.addToBackStack(null)

                            transaction.commit()
                        }
                        else -> {
                            val newFragment: Fragment = FragmentStatsLeastWorn()
                            val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

                            transaction.add((getView()!!.parent as ViewGroup).id, newFragment)
                            transaction.addToBackStack(null)

                            transaction.commit()
                        }
                    }
                }
            })
        }
        }
}

                /*
                if (i < 2) {
                    Toast.makeText(activity, i.toString(), Toast.LENGTH_SHORT).show()
                }
                else {
                    // Create new fragment and transaction
                    val newFragment: Fragment = FragmentStatsMostWorn()
                    // consider using Java coding conventions (upper first char class names!!!)
                    // consider using Java coding conventions (upper first char class names!!!)
                    val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()

                    // Replace whatever is in the fragment_container view with this fragment,
                    // and add the transaction to the back stack
                    transaction.add((getView()!!.parent as ViewGroup).id, newFragment)
                    transaction.addToBackStack(null)

                    // Commit the transaction
                    transaction.commit()
                }
                //val intent = Intent(activity, AddItem::class.java)
                //startActivity(intent)
            }

        })*/
        /*
        val adapter2 = ArrayAdapter(activity, R.layout.stat_list_row, R.id.stat_row_text_view/*android.R.layout.simple_list_item_1*/, statListItems)
        stats_list_view2.adapter = adapter2
         */
