package com.example.miniclo

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ItemAdapter(private val items: ArrayList<Item>) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.get().load(items[position].image).into(holder.image)
        holder.title.text = items[position].category
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

    override fun getItemCount() = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.rec_item_photo)
        val title: TextView = itemView.findViewById(R.id.rec_item_title)
    }
}

