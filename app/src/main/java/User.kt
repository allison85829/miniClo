package com.example.miniclo

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.Exclude
import java.util.HashMap

@IgnoreExtraProperties
data class User(
    var email: String = "",
    var uid: String = "",
    var item_list: List<String> = listOf<String>(),
    var total_item: Int = 0
) {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "email" to email,
            "uid" to uid,
            "item_list" to item_list,
            "total_item" to total_item
        )
    }
}