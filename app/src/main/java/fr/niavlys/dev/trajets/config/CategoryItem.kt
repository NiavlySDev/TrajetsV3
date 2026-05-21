package fr.niavlys.dev.trajets.config

import org.json.JSONObject

data class CategoryItem(
    val id: String,
    var label: String,
    var order: Int = 0
) {
    fun toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("label", label)
        .put("order", order)

    companion object {
        fun fromJson(json: JSONObject): CategoryItem = CategoryItem(
            id = json.getString("id"),
            label = json.getString("label"),
            order = json.optInt("order", 0)
        )
    }
}
