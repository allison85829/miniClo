package com.example.miniclo

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_item_detail_page.*

class ItemDetailPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail_page)

        val item : Item = intent.getParcelableExtra("item_obj")
        item_tags_value.text = item.tags.toString()
        item_times_worn_value.text = item.worn_frequency.toString()
        item_date_added_value.text = item.date_added
        //item_name.text = item.category
        Picasso.get().load(item.image).into(item_img)

        laundry_btn.setOnClickListener{
            AlertDialog.Builder(this)
                .setMessage("Hi!")
                .create()
                .show()
        }

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Closet Item"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}