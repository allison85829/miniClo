package com.example.miniclo


//import kotlinx.android.synthetic.main.activity_main.category
//import kotlinx.android.synthetic.main.activity_main.date_added
//import kotlinx.android.synthetic.main.activity_main.tags

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_bottom_nav_fragment_closet.*

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
    val REQUEST_TO_DETAIL = 3

//    lateinit var category : TextView
//    lateinit var date_added : TextView
//    lateinit var tags : TextView
//    lateinit var laundry_status : TextView
//    lateinit var worn_frequency : TextView
    lateinit var image_view : ImageView
    lateinit var item_card : CardView
    var item_key = "-M6kY-zQC4wNFAr-5ljK"

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
//        initialize all views
        if (view != null) {
//            tags = getView()!!.findViewById(R.id.tags_text)
//            date_added = getView()!!.findViewById(R.id.date_added_text)
//            worn_frequency = getView()!!.findViewById(R.id.worn_frequency)
//            laundry_status = getView()!!.findViewById(R.id.laundry_status)
            image_view = getView()!!.findViewById<ImageView>(R.id.item_img)
            item_card = getView()!!.findViewById<CardView>(R.id.item_card)
        }

        setUpToolbar()
        setUpSearchbar()
    }

    public override fun onStart() {
        super.onStart()

        val itemListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                item_key = "-M6kY-zQC4wNFAr-5ljK"
                val item: Item? = dataSnapshot.child(item_key).getValue<Item>()
//                category.text = item?.category
//                date_added.text = item?.date_added
//                var t = ""
//                for (tag in item?.tags!!) {
//                    t += tag + ", "
//                }
//                tags.text = t
//                tags.text = item?.category
//                laundry_status.text = item?.laundry_status.toString()
//                worn_frequency.text = item?.worn_frequency.toString()
                Glide.with(this@BottomNavFragmentCloset)
                        .load(item!!.image)
                        .into(image_view)
            }
        }

        itemReference = Firebase.database.reference.child("/items")
        itemReference.addValueEventListener(itemListener)
        this.itemListener = itemListener

        item_card.setOnClickListener {
            val intent = Intent(activity, ItemDetail::class.java)
            intent.putExtra("item_key", item_key)
            val res = resources
            startActivityForResult(intent, REQUEST_TO_DETAIL)
        }

        add_item_button.setOnClickListener{
            val intent = Intent(activity, AddItem::class.java)
            startActivity(intent)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_TO_DETAIL && resultCode == RESULT_OK) {

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

    fun setUpToolbar() {
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
                R.id.action_del -> {
                    // do something
                    Toast.makeText(
                        activity,
                        "Clicked Delete",
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

    fun setUpSearchbar() {
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
}
