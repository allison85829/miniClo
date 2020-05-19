package com.example.miniclo


import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_bottom_nav_fragment_laundry.*


/**
 * A simple [Fragment] subclass.
 */
class BottomNavFragmentLaundry : androidx.fragment.app.Fragment() {

    private var mDatabase : FirebaseDatabase = FirebaseDatabase.getInstance()
    private var itemsReference : DatabaseReference = mDatabase.reference.child("/items");
    private lateinit var itemsListener: ValueEventListener

    override fun onStart() {
        super.onStart()

        val itemListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val items = dataSnapshot!!.children
                val itemsArr : ArrayList<Item> = ArrayList<Item>()
                //val items  = dataSnapshot.getValue<Item>();
//                category.text = item?.category
                items.forEach {
                    //Log.i("Item", it.toString())
                    val item: Item? = it.getValue<Item>()
                    Log.i("Item", item.toString())
                    if (item != null) {
                        itemsArr.add(item)
                    }
                }
                setupRecyclerView(itemsArr)
            }
        }

        itemsReference.addListenerForSingleValueEvent(itemListener)
        this.itemsListener = itemListener
    }

    override fun onStop() {
        super.onStop()
        itemsReference.removeEventListener(this.itemsListener)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        toolbar_laundry?.title = "My Closet"
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(toolbar_laundry)
        toolbar_laundry?.title = "My Closet"
        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_nav_fragment_laundry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        //setupRecyclerView()
        //setupSearchbar()
    }

    fun setupToolbar() {
        toolbar_laundry.inflateMenu(R.menu.laundry_menu_options)
        toolbar_laundry.setOnMenuItemClickListener {
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
    }

    fun setupRecyclerView(itemArr: ArrayList<Item>) {
        /*
        val itemArr = arrayListOf<ItemTest>()

        for (i in 0..100) {
            itemArr.add(ItemTest("Test $i", "https://via.placeholder.com/350/CCCCCC/ff0000"))
        }
         */

        //var layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        laundryRecView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = ItemAdapter(itemArr)
        }
    }

    /*
    fun setupSearchbar() {
        //REFERENCE MATERIALSEARCHBAR AND LISTVIEW
        //val lv = android.R.layout.mListView as ListView
        val lv = mListView
        //val searchBar = android.R.layout.searchBar as MaterialSearchBar
        val searchBar = searchBar
        searchBar.setHint("Search..")
        searchBar.setSpeechMode(true)

        var galaxies = arrayOf("Sombrero", "Cartwheel", "Pinwheel", "StarBust", "Whirlpool", "Ring Nebular", "Own Nebular", "Centaurus A", "Virgo Stellar Stream", "Canis Majos Overdensity", "Mayall's Object", "Leo", "Milky Way", "IC 1011", "Messier 81", "Andromeda", "Messier 87")

        //ADAPTER
        val adapter = ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, galaxies)
        lv?.setAdapter(adapter)

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
        lv?.setOnItemClickListener(object : AdapterView.OnItemClickListener {
            override fun onItemClick(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                Toast.makeText(activity, "Hi"/*adapter.getItem(i)!!.toString()*/, Toast.LENGTH_SHORT).show()
            }
        })
    }
    */

    fun setupBackButton() {
        // Set up back button
        toolbar_laundry.setNavigationIcon(R.drawable.ic_home_black_24dp) // need to set the icon here to have a navigation icon. You can simple create an vector image by "Vector Asset" and using here
        toolbar_laundry.setNavigationOnClickListener {
            // do something when click navigation
        }
    }

    /*
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.closet_menu_options, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
     */
}
