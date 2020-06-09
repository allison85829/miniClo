package com.example.miniclo

import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class Item(
    var category: String = "",
    var date_added: String = "",
    var img_url: String = "",
    var img_name : String = "",
    var laundry_status: Boolean = false,
    var worn_frequency: Int = 0,
    var tags: List<String> = listOf<String>(),
    var user: String = "",
    var key: String = ""
) : Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "category" to category,
            "date_added" to date_added,
            "img_url" to img_url,
            "img_name" to img_name,
            "laundry_status" to laundry_status,
            "worn_frequency" to worn_frequency,
            "tags" to tags,
            "user" to user,
            "key" to key
        )
    }
}