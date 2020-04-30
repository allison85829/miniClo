package com.example.miniclo


import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_bottom_nav_fragment_closet.*

import android.widget.AdapterView
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.mancj.materialsearchbar.MaterialSearchBar

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.EditText
import androidx.constraintlayout.solver.widgets.Snapshot
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

//import kotlinx.android.synthetic.main.activity_main.category
//import kotlinx.android.synthetic.main.activity_main.date_added
//import kotlinx.android.synthetic.main.activity_main.tags

import java.io.File

import android.os.Handler

/**
 * A simple [Fragment] subclass.
 */
class BottomNavFragmentCloset : androidx.fragment.app.Fragment() {
    //        create an instance of Firebase Database
    private var mDatabase : FirebaseDatabase = FirebaseDatabase.getInstance()
    //        reference to the root node
    private lateinit var itemReference : DatabaseReference

    private lateinit var itemListener: ValueEventListener

    private lateinit var storage: FirebaseStorage

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        toolbar_closet?.title = "My Closet"
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(toolbar_closet)
        toolbar_closet?.title = "My Closet"
        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_nav_fragment_closet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up back button
        /*toolbar_closet.setNavigationIcon(R.drawable.ic_home_black_24dp) // need to set the icon here to have a navigation icon. You can simple create an vector image by "Vector Asset" and using here
        toolbar_closet.setNavigationOnClickListener {
            // do something when click navigation
        }*/

        toolbar_closet.inflateMenu(R.menu.closet_menu_options)
        toolbar_closet.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_filter -> {
                    // do something
                    // build alert dialog
                    val dialogBuilder = AlertDialog.Builder(activity)

                    // set message of alert dialog
                    dialogBuilder.setMessage("Testing Alert Dialog")
                        // if the dialog is cancelable
                        .setCancelable(false)
                        // positive button text and action
                        .setPositiveButton("Proceed", DialogInterface.OnClickListener {
                                dialog, id -> dialog.cancel()
                        })
                        // negative button text and action
                        .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                                dialog, id -> dialog.cancel()
                        })

                    // create dialog box
                    val alert = dialogBuilder.create()
                    // set title for alert dialog box
                    alert.setTitle("AlertDialogExample")
                    // show alert dialog
                    alert.show()
                    true
                }
                R.id.action_options -> {
                    // do something
                    Toast.makeText(
                        activity,
                        "Clicked Options Menu",
                        Toast.LENGTH_SHORT
                    ).show()
                    true
                }
                else -> {
                    super.onOptionsItemSelected(it)
                }
            }
        }

        //REFERENCE MATERIALSEARCHBAR AND LISTVIEW
        //val lv = android.R.layout.mListView as ListView
//        val lv = mListView
        //val searchBar = android.R.layout.searchBar as MaterialSearchBar
        val searchBar = searchBar
        searchBar.setHint("Search..")
        searchBar.setSpeechMode(true)

        var galaxies = arrayOf("Sombrero", "Cartwheel", "Pinwheel", "StarBust", "Whirlpool", "Ring Nebular", "Own Nebular", "Centaurus A", "Virgo Stellar Stream", "Canis Majos Overdensity", "Mayall's Object", "Leo", "Milky Way", "IC 1011", "Messier 81", "Andromeda", "Messier 87")

        //ADAPTER
        val adapter = ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, galaxies)
//        lv?.setAdapter(adapter)

        //SEARCHBAR TEXT CHANGE LISTENER
        searchBar?.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                //SEARCH FILTER
                adapter.getFilter().filter(charSequence)
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })

        //LISTVIEW ITEM CLICKED
//        lv?.setOnItemClickListener(object : AdapterView.OnItemClickListener {
//            override fun onItemClick(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
//                Toast.makeText(activity, "Hi"/*adapter.getItem(i)!!.toString()*/, Toast.LENGTH_SHORT).show()
//            }
//        })

        //end
    }

    public override fun onStart() {
        super.onStart()

        val itemListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val item: Item? = dataSnapshot.child("1").getValue<Item>()
//                category.text = item?.category
//                date_added.text = item?.date_added
//                var t = ""
//                for (tag in item?.tags!!) {
//                    t += tag + ", "
//                }
//                tags.text = t
//                laundry_status.text = item?.laundry_status.toString()
//                worn_frequency.text = item?.worn_frequency.toString()
            }
        }

        itemReference = Firebase.database.reference.child("/items")
        itemReference.addValueEventListener(itemListener)
        this.itemListener = itemListener
        val tags_lst = listOf<String>("Red", "White", "Short-sleeve")
        val new_item : Item = Item("Dress", "04-21-2020", "img3.jpg", false, 10, tags_lst)
        itemReference.child("3").setValue(new_item)

        add_item_button.setOnClickListener{
            val intent = Intent(activity, AddItem::class.java)
            startActivity(intent)
        }
    }

    public override fun onStop() {
        super.onStop()
        itemReference.removeEventListener(this.itemListener)
    }

    /*
    fun addItem(view: View) {
        val intent = Intent(activity, AddItem::class.java)
        startActivity(intent)
    }
    */
    /*
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.closet_menu_options, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
     */
}
