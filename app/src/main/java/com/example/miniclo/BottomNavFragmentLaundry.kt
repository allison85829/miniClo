package com.example.miniclo


import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.mancj.materialsearchbar.MaterialSearchBar
import kotlinx.android.synthetic.main.fragment_bottom_nav_fragment_laundry.*
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class BottomNavFragmentLaundry : androidx.fragment.app.Fragment() {

    private var mDatabase : FirebaseDatabase = FirebaseDatabase.getInstance()
    private var itemsReference : DatabaseReference = mDatabase.reference.child("/items")
    private lateinit var itemsListener: ValueEventListener
    private var userReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("/users")
    private var userUid : String = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private var SORT_BY_WORN_FREQ_ASC = "sort by worn frequency asc"
    private var SORT_BY_WORN_FREQ_DESC = "sort by worn frequency desc"
    private var SORT_BY_DATE_ADDED = "sort by date added"
    private lateinit var laundry_items : ArrayList<Item>

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
                    val item: Item? = it.getValue<Item>()
                    if (item != null) {
                        if (item.laundry_status) {
                            item.key = it.key!!
                            itemsArr.add(item)
                        }
                    }
                }
                laundry_items = itemsArr
                setupRecyclerView(itemsArr)
            }
        }

        itemsReference.orderByChild("user").equalTo(userUid).addListenerForSingleValueEvent(itemListener)
        this.itemsListener = itemListener
    }

    override fun onStop() {
        super.onStop()
        itemsReference.removeEventListener(this.itemsListener)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        setHasOptionsMenu(true)
        (activity as AppCompatActivity).setSupportActionBar(toolbar_laundry)
        setHasOptionsMenu(true)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_nav_fragment_laundry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clearLaundryBtn.setOnClickListener{
            val dialogBuilder = AlertDialog.Builder(activity)
            dialogBuilder.setMessage("Are you sure want to clear your laundry?")
                .setCancelable(false)
                .setPositiveButton("Clear", DialogInterface.OnClickListener {
                        dialog, id ->
                    run {
                        dialog.cancel()
                        clearLaundry()
                        Toast.makeText(context, "Successfully cleared laundry", Toast.LENGTH_SHORT).show()
                    }

                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener {
                        dialog, id -> dialog.cancel()
                })

            val alert = dialogBuilder.create()
            alert.setTitle("Clear Laundry")
            alert.show()
        }
        setupToolbar()
    }

    fun setupToolbar() {
        toolbar_laundry.inflateMenu(R.menu.laundry_menu_options)
        toolbar_laundry.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.sort_by_option -> {
                    val sort_options = arrayOf("Date Added", "Most Frequently Worn", "Least Frequently Worn")
                    val builder = AlertDialog.Builder(context)
                    with(builder)
                    {
                        setTitle("Sort By")
                        setItems(sort_options) { dialog, which ->
                            when (which) {
                                0 -> sortRecView(SORT_BY_DATE_ADDED)
                                1 -> sortRecView(SORT_BY_WORN_FREQ_DESC)
                                2 -> sortRecView(SORT_BY_WORN_FREQ_ASC)
                            }
                            dialog.dismiss()
                            Toast.makeText(
                                context,
                                "Sorted by " + sort_options[which],
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        show()
                    }
                    true
                }
                R.id.filter_by_option -> {
                    val filter_options = arrayOf("Top", "Bottom", "Shoe", "Dress", "Hat", "Accessory", "Other")
                    val builder = AlertDialog.Builder(context)
                    with(builder)
                    {
                        setTitle("Filter By")
                        setItems(filter_options) { dialog, which ->
                            filterRecView(filter_options[which])
                            dialog.dismiss()
                            Toast.makeText(
                                context,
                                "Filtered by " + filter_options[which],
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        show()
                    }
                    true
                }
                else -> {
                    val itemListener = object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            TODO("not implemented")
                        }

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val items = dataSnapshot!!.children
                            val itemsArr : ArrayList<Item> = ArrayList<Item>()
                            items.forEach {
                                val item: Item? = it.getValue<Item>()
                                if (item != null) {
                                    if (item.laundry_status) {
                                        item.key = it.key!!
                                        itemsArr.add(item)
                                    }
                                }
                            }
                            setupRecyclerView(itemsArr)
                        }
                    }

                    itemsReference.orderByChild("user").equalTo(userUid).addListenerForSingleValueEvent(itemListener)
                    true
                }
            }
        }
    }

    fun clearLaundry() {
        for (item in laundry_items) {
            itemsReference.child(item.key).child("laundry_status").setValue(false)
        }
        userReference.child("$userUid/laundry_list/").setValue(null)
        laundry_items = ArrayList<Item>()
        setupRecyclerView(laundry_items)

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
        })
         */
    }

    fun setupRecyclerView(itemArr: ArrayList<Item>) {
        var itemAdapter = ItemAdapter(itemArr)
        laundryRecView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = itemAdapter
        }
        setupSearchBar(itemArr, itemAdapter)
    }

    fun setupSearchBar(itemArr: ArrayList<Item>, itemAdapter: ItemAdapter) {
        searchBarLaundry.addTextChangeListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                itemAdapter.getFilter().filter(charSequence)
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        searchBarLaundry.setOnSearchActionListener( object : MaterialSearchBar.OnSearchActionListener {
            override fun onButtonClicked(buttonCode: Int) {
                Log.i("button code", buttonCode.toString())
            }

            override fun onSearchStateChanged(enabled: Boolean) {
                if (!enabled) {
                    laundryRecView.apply {
                        layoutManager = GridLayoutManager(context, 2)
                        adapter = itemAdapter
                    }
                }
            }

            override fun onSearchConfirmed(text: CharSequence) {
                searchItems(itemArr, text.toString())
            }
        })
    }

    fun searchItems(itemArr: ArrayList<Item>, text: String) {
        searchBarLaundry.disableSearch()
        var foundItems = ArrayList<Item>()
        val textLower = text.toLowerCase(Locale.ROOT)

        for (item in itemArr) {
            var itemTagsLower = item.tags.map{it.toLowerCase()}
            if (item.category.toLowerCase(Locale.ROOT).contains(textLower) ||
                itemTagsLower.any{ it.contains(textLower) }/*textLower in itemTagsLower*/) {
                foundItems.add(item)
            }
        }

        var itemAdapter = ItemAdapter(foundItems)
        laundryRecView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = itemAdapter
        }
    }

    fun sortRecView(sort_prop : String) {
        val itemsArr = laundry_items.map{it.copy()} as ArrayList<Item>
        when (sort_prop) {
            SORT_BY_DATE_ADDED -> itemsArr.sortBy{ it.date_added }
            SORT_BY_WORN_FREQ_ASC -> itemsArr.sortBy{ it.worn_frequency }
            SORT_BY_WORN_FREQ_DESC -> itemsArr.sortByDescending { it.worn_frequency }
        }
        setupRecyclerView(itemsArr)
    }

    fun filterRecView(filter_prop : String) {
        var itemsArr = ArrayList<Item>()

        for (item in laundry_items) {
            if (item.category == filter_prop) {
                itemsArr.add(item)
            }
        }
        setupRecyclerView(itemsArr)
    }
}