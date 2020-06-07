package com.example.miniclo

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class ItemAdapter(private val items: ArrayList<Item>) : RecyclerView.Adapter<ItemAdapter.ViewHolder>(), Filterable {
    var filteredItems = ArrayList<Item>()

    init {
        filteredItems = items
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.get().load(filteredItems[position].image).into(holder.image)
        //holder.title.text = filteredItems[position].category
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rec_item_row, parent, false)
        val holder = ViewHolder(view)
        view.setOnClickListener{
            val intent = Intent(parent.context, ItemDetailPage::class.java)
            intent.putExtra("item_obj", items[holder.adapterPosition])
            parent.context.startActivity(intent)
        }
        return holder
    }

    override fun getItemCount() = filteredItems.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    filteredItems = items
                } else {
                    val resultList = ArrayList<Item>()
                    val textLower = charSearch.toLowerCase(Locale.ROOT)
                    for (item in items) {
                        var itemTagsLower = item.tags.map{it.toLowerCase()}
                        if (item.category.toLowerCase(Locale.ROOT).contains(textLower) ||
                            itemTagsLower.any{ it.contains(textLower) }) {
                            resultList.add(item)
                        }
                    }
                    filteredItems = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredItems
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItems = results?.values as ArrayList<Item>
                notifyDataSetChanged()
            }

        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.rec_item_photo)
        //val title: TextView = itemView.findViewById(R.id.rec_item_title)
    }
}

