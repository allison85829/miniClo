package com.example.miniclo.com.example.miniclo

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.Exclude

@IgnoreExtraProperties
data class User(
    var user_name: String = "",
    var item_list: MutableMap<String, Boolean> = HashMap(),
    var laundry_list: MutableMap<String, Boolean> = HashMap()
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "user_name" to user_name,
            "item_list" to item_list,
            "laundry_list" to laundry_list
        )
    }
}