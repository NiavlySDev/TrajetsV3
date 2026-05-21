package fr.niavlys.dev.trajets.config

import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.UUID

class ConfigRepository(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun load(): AppConfig {
        val raw = preferences.getString(KEY_CONFIG, null) ?: return defaults().also(::save)

        return try {
            val root = JSONObject(raw)
            AppConfig(
                transports = root.getJSONArray("transports").toConfigItems(),
                stations = root.getJSONArray("stations").toConfigItems(),
                contacts = root.getJSONArray("contacts").toContacts()
            )
        } catch (_: JSONException) {
            defaults().also(::save)
        }
    }

    fun save(config: AppConfig) {
        val root = JSONObject()
            .put("transports", config.transports.toConfigJsonArray())
            .put("stations", config.stations.toConfigJsonArray())
            .put("contacts", config.contacts.toContactJsonArray())
        preferences.edit().putString(KEY_CONFIG, root.toString()).apply()
    }

    fun newId(): String = UUID.randomUUID().toString()

    fun resetToDefaults() {
        save(defaults())
    }

    private fun defaults(): AppConfig = AppConfig().apply {
        transports += configItem("Tramway 4", "Tramway 4")
        transports += configItem("Transilien", "Transilien")
        transports += configItem("Nomad", "Nomad")
        transports += configItem("Métro 4", "Métro 4")
        transports += configItem("Métro 14", "Métro 14")
        transports += configItem("RER C", "RER C")
        transports += configItem("RER D", "RER D")
        transports += configItem("Bus 13", "Bus 13")
        transports += configItem("Bus 16", "Bus 16")
        transports += configItem("Bus 17", "Bus 17")
        transports += configItem("Bus 305", "Bus 305")

        stations += configItem("Evreux", "la Gare D'Evreux")
        stations += configItem("Saint Lazare", "Saint Lazare")
        stations += configItem("BFM", "Bibliotheque François Miterrand")
        stations += configItem("Juvisy", "Juvisy")
        stations += configItem("Danton", "Danton")
        stations += configItem("Saint Michel", "Saint Michel Notre Dame")
        stations += configItem("Montparnasse", "Montparnasse")
        stations += configItem("Houdan", "Houdan")
        stations += configItem("V-Chantiers", "Versailles Chantiers")
        stations += configItem("V-Chateau", "Versailles Chateau Rive Gauche")
        stations += configItem("Austerlitz", "Paris Austerlitz")
        stations += configItem("GDL", "Gare de Lyon")
        stations += configItem("Bercy", "Bercy")

        contacts += ContactItem(newId(), "Maman", "0682377140")
        contacts += ContactItem(newId(), "Papa", "0637038218")
        contacts += ContactItem(newId(), "Patou", "0686525035")
    }

    private fun configItem(label: String, phrase: String): ConfigItem = ConfigItem(newId(), label, phrase)

    private fun JSONArray.toConfigItems(): MutableList<ConfigItem> =
        MutableList(length()) { index -> ConfigItem.fromJson(getJSONObject(index)) }

    private fun JSONArray.toContacts(): MutableList<ContactItem> =
        MutableList(length()) { index -> ContactItem.fromJson(getJSONObject(index)) }

    private fun List<ConfigItem>.toConfigJsonArray(): JSONArray = JSONArray().also { array ->
        forEach { item -> array.put(item.toJson()) }
    }

    private fun List<ContactItem>.toContactJsonArray(): JSONArray = JSONArray().also { array ->
        forEach { contact -> array.put(contact.toJson()) }
    }

    companion object {
        private const val PREFS = "trajets_config"
        private const val KEY_CONFIG = "config"
    }
}
