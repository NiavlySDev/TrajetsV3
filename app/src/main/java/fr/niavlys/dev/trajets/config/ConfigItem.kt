package fr.niavlys.dev.trajets.config

import org.json.JSONObject

data class ConfigItem(
    val id: String,
    var label: String,
    var phrase: String
) {
    fun toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("label", label)
        .put("phrase", phrase)

    companion object {
        fun fromJson(json: JSONObject): ConfigItem = ConfigItem(
            id = json.getString("id"),
            label = json.getString("label"),
            phrase = json.getString("phrase")
        )
    }
}
