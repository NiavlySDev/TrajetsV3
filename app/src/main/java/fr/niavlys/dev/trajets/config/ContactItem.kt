package fr.niavlys.dev.trajets.config

import org.json.JSONObject

data class ContactItem(
    val id: String,
    var name: String,
    var phone: String
) {
    fun toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("name", name)
        .put("phone", phone)

    companion object {
        fun fromJson(json: JSONObject): ContactItem = ContactItem(
            id = json.getString("id"),
            name = json.getString("name"),
            phone = json.getString("phone")
        )
    }
}
