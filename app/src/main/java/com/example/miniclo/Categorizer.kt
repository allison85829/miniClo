package com.example.miniclo

import android.util.Log

class Categorizer() {
    private var categories =
        listOf<String>("Top", "Bottom", "Hat", "Dress", "Shoe", "Accessory", "Other")
    private val cat_dict = mapOf<String, List<String>>(
        "Top" to listOf<String>("top", "t-shirt", "sleeve", "top", "active shirt",
            "hoodie", "hood", "sweatshirt", "jacket", "shirt", "blouse", "collar"),
        "Bottom" to listOf<String>("bottom", "trousers", "leg", "jeans", "active pants",
            "denim", "sweatpant", "legging", "shorts", "jean short", "trunks"),
        "Hat" to listOf<String>("beanie", "cap", "knit cap", "headgear", "bonnet",
            "sun hat", "hat", "costume hat", "baseball cap", "bucket hat",
            "trucker hat", "cricket hat", "fedora"),
        "Dress" to listOf<String>("dress", "day dress", "robe", "sheath dress",
            "cocktail", "skirt", "pencil skirt"),
        "Shoe" to listOf<String>("shoe", "footwear", "sneakers", "outdoor shoe",
            "running shoe", "tennis shoe", "athletic shoe", "plimsoll shoe",
            "walking shoe", "hiking shoe", "boot", "hiking boot", "high heels",
            "espadrille", "sandal", "slingback", "flip-flops", "slide sandal"),
        "Accessory" to listOf<String>("glasses", "belt", "socks"),
        "Others" to listOf<String>()
    )

    // get the main category name
    public fun getCategory(labels : ArrayList<String>) : String {
        var category = ""
        var found = false

        for (label in labels) {
            for ((cat, cat_word) in cat_dict) {
                if (cat_word.contains(label.toLowerCase())) {
                    category = cat
                    labels.remove(category)
                    labels.remove("Clothing")
                    found = true
                    break
                }
            }
            if (found) {
                break
            }
        }
        return category
    }


}