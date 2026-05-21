package fr.niavlys.dev.trajets.config

import org.json.JSONObject

data class ConfigItem(
    val id: String,
    var label: String,
    var phrase: String,
    var order: Int = 0,
    var categoryId: String? = null
) {
    fun toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("label", label)
        .put("phrase", phrase)
        .put("order", order)
        .put("categoryId", categoryId)

    companion object {
        fun fromJson(json: JSONObject): ConfigItem = ConfigItem(
            id = json.getString("id"),
            label = json.getString("label"),
            phrase = json.getString("phrase"),
            order = json.optInt("order", 0),
            categoryId = json.optString("categoryId").ifBlank { null }
        )
    }
}
