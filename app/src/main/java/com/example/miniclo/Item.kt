package com.example.miniclo

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.HashMap

@IgnoreExtraProperties
data class Item(
    var category: String = "",
    var date_added: String = "",
    var image: String = "",
    var laundry_status: Boolean = false,
    var worn_frequency: Int = 0,
    var tags: List<String> = listOf<String>(),
    var user: String = ""
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "category" to category,
            "date_added" to date_added,
            "image" to image,
            "laundry_status" to laundry_status,
            "worn_frequency" to worn_frequency,
            "tags" to tags,
            "user" to user
        )
    }
}