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
import kotlinx.android.synthetic.main.fragment_bottom_nav_fragment_laundry.*


/**
 * A simple [Fragment] subclass.
 */
class BottomNavFragmentLaundry : androidx.fragment.app.Fragment() {

    private var mDatabase : FirebaseDatabase = FirebaseDatabase.getInstance()
    private var itemsReference : DatabaseReference = mDatabase.reference.child("/items");
    private lateinit var itemsListener: ValueEventListener
    private var user : String = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private var SORT_BY_WORN_FREQ_ASC = "sort by worn frequency asc"
    private var SORT_BY_WORN_FREQ_DESC = "sort by worn frequency desc"
    private var SORT_BY_DATE_ADDED = "sort by date added"

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
                setupRecyclerView(itemsArr)
            }
        }

        itemsReference.orderByChild("user").equalTo(user).addListenerForSingleValueEvent(itemListener)
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

        clearLaundryBtn.setOnClickListener{
            Toast.makeText(context, "Clear Laundry is clicked", Toast.LENGTH_SHORT).show()
        }
        setupToolbar()
        //setupRecyclerView()
        //setupSearchbar()
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

                    itemsReference.orderByChild("user").equalTo(user).addListenerForSingleValueEvent(itemListener)
                    true
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

    fun sortRecView(sort_prop : String) {
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
                when (sort_prop) {
                    SORT_BY_DATE_ADDED -> itemsArr.sortBy{ it.date_added }
                    SORT_BY_WORN_FREQ_ASC -> itemsArr.sortBy{ it.worn_frequency }
                    SORT_BY_WORN_FREQ_DESC -> itemsArr.sortByDescending { it.worn_frequency }
                }

                setupRecyclerView(itemsArr)
            }
        }
        itemsReference.orderByChild("user").equalTo(user).addListenerForSingleValueEvent(itemListener)
        itemsListener = itemsListener
    }

    fun filterRecView(filter_prop : String) {
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
                        if (item.laundry_status && item.category == filter_prop) {
                            item.key = it.key!!
                            itemsArr.add(item)
                        }
                    }
                }
                setupRecyclerView(itemsArr)
            }
        }
        itemsReference.orderByChild("user").equalTo(user).addListenerForSingleValueEvent(itemListener)
    }

    /*
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.closet_menu_options, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
     */
}