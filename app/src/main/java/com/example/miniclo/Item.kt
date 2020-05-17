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
    var image: String = "",
    var laundry_status: Boolean = false,
    var worn_frequency: Int = 0,
    var tags: List<String> = listOf<String>(),
    var user: String = "",
    var incr: Boolean = false
) : Parcelable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "category" to category,
            "date_added" to date_added,
            "image" to image,
            "laundry_status" to laundry_status,
            "worn_frequency" to worn_frequency,
            "tags" to tags,
            "user" to user,
            "incr" to incr
        )
    }
}