package fr.niavlys.dev.trajets.config

import org.json.JSONArray
import org.json.JSONObject

data class ContactGroupItem(
    val id: String,
    var label: String,
    var contactIds: MutableList<String> = mutableListOf(),
    var order: Int = 0
) {
    fun toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("label", label)
        .put("contactIds", JSONArray().also { array -> contactIds.forEach(array::put) })
        .put("order", order)

    companion object {
        fun fromJson(json: JSONObject): ContactGroupItem = ContactGroupItem(
            id = json.getString("id"),
            label = json.getString("label"),
            contactIds = json.optJSONArray("contactIds")?.let { array ->
                MutableList(array.length()) { index -> array.getString(index) }
            } ?: mutableListOf(),
            order = json.optInt("order", 0)
        )
    }
}
