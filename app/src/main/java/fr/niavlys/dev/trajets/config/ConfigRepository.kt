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
                transportCategories = root.optJSONArray("transportCategories")?.toCategories() ?: defaultCategories(),
                transports = root.optJSONArray("transports")?.toConfigItems() ?: mutableListOf(),
                stations = root.optJSONArray("stations")?.toConfigItems() ?: mutableListOf(),
                contacts = root.optJSONArray("contacts")?.toContacts() ?: mutableListOf(),
                contactGroups = root.optJSONArray("contactGroups")?.toContactGroups() ?: mutableListOf(),
                messagePresets = (
                    root.optJSONArray("messagePresets")?.toPresets() ?: mutableListOf()
                ).apply {
                    root.optJSONArray("routePresets")?.toPresets()?.let { addAll(it) }
                }
            ).withMigrationDefaults().also(::save)
        } catch (_: JSONException) {
            defaults().also(::save)
        }
    }

    fun save(config: AppConfig) {
        val root = JSONObject()
            .put("transportCategories", config.transportCategories.toCategoryJsonArray())
            .put("transports", config.transports.toConfigJsonArray())
            .put("stations", config.stations.toConfigJsonArray())
            .put("contacts", config.contacts.toContactJsonArray())
            .put("contactGroups", config.contactGroups.toContactGroupJsonArray())
            .put("messagePresets", config.messagePresets.toPresetJsonArray())
        preferences.edit().putString(KEY_CONFIG, root.toString()).apply()
    }

    fun newId(): String = UUID.randomUUID().toString()

    fun resetToDefaults() {
        save(defaults())
    }

    private fun defaults(): AppConfig = AppConfig().apply {
        transportCategories += defaultCategories()
        val tram = categoryId("Tramway")
        val train = categoryId("Train")
        val metro = categoryId("Métro")
        val rer = categoryId("RER")
        val bus = categoryId("Bus")

        transports += configItem("Tramway 4", "Tramway 4", 10, tram)
        transports += configItem("Transilien", "Transilien", 20, train)
        transports += configItem("Nomad", "Nomad", 30, train)
        transports += configItem("Métro 4", "Métro 4", 40, metro)
        transports += configItem("Métro 14", "Métro 14", 50, metro)
        transports += configItem("RER C", "RER C", 60, rer)
        transports += configItem("RER D", "RER D", 70, rer)
        transports += configItem("Bus 13", "Bus 13", 80, bus)
        transports += configItem("Bus 16", "Bus 16", 90, bus)
        transports += configItem("Bus 17", "Bus 17", 100, bus)
        transports += configItem("Bus 305", "Bus 305", 110, bus)

        stations += configItem("Evreux", "la Gare D'Evreux", 10)
        stations += configItem("Saint Lazare", "Saint Lazare", 20)
        stations += configItem("BFM", "Bibliotheque François Miterrand", 30)
        stations += configItem("Juvisy", "Juvisy", 40)
        stations += configItem("Danton", "Danton", 50)
        stations += configItem("Saint Michel", "Saint Michel Notre Dame", 60)
        stations += configItem("Montparnasse", "Montparnasse", 70)
        stations += configItem("Houdan", "Houdan", 80)
        stations += configItem("V-Chantiers", "Versailles Chantiers", 90)
        stations += configItem("V-Chateau", "Versailles Chateau Rive Gauche", 100)
        stations += configItem("Austerlitz", "Paris Austerlitz", 110)
        stations += configItem("GDL", "Gare de Lyon", 120)
        stations += configItem("Bercy", "Bercy", 130)

        contacts += ContactItem(newId(), "Maman", "0682377140")
        contacts += ContactItem(newId(), "Papa", "0637038218")
        contacts += ContactItem(newId(), "Patou", "0686525035")

        contactGroups += ContactGroupItem(newId(), "Famille", contacts.map { it.id }.toMutableList(), 10)
        messagePresets += PresetItem(newId(), "Je pars", mutableListOf(MessageBlock(BlockType.ACTION, text = "Je pars maintenant")), 10)
        messagePresets += PresetItem(newId(), "Je suis arrivé", mutableListOf(MessageBlock(BlockType.ACTION, text = "Je suis arrivé")), 20)
    }

    private fun AppConfig.categoryId(label: String): String? =
        transportCategories.firstOrNull { it.label == label }?.id

    private fun configItem(label: String, phrase: String, order: Int, categoryId: String? = null): ConfigItem =
        ConfigItem(newId(), label, phrase, order, categoryId)

    private fun defaultCategories(): MutableList<CategoryItem> = mutableListOf(
        CategoryItem(newId(), "Tramway", 10),
        CategoryItem(newId(), "Train", 20),
        CategoryItem(newId(), "Métro", 30),
        CategoryItem(newId(), "RER", 40),
        CategoryItem(newId(), "Bus", 50)
    )

    private fun AppConfig.withMigrationDefaults(): AppConfig {
        if (transportCategories.isEmpty()) transportCategories += defaultCategories()
        transports.forEachIndexed { index, item ->
            if (item.order == 0) item.order = (index + 1) * 10
            if (item.categoryId == null) {
                item.categoryId = guessCategory(item.label)
            }
        }
        stations.forEachIndexed { index, item -> if (item.order == 0) item.order = (index + 1) * 10 }
        return this
    }

    private fun AppConfig.guessCategory(label: String): String? {
        val normalized = label.lowercase()
        val category = when {
            "tram" in normalized -> "Tramway"
            "bus" in normalized -> "Bus"
            "métro" in normalized || "metro" in normalized -> "Métro"
            "rer" in normalized -> "RER"
            else -> "Train"
        }
        return categoryId(category)
    }

    private fun JSONArray.toConfigItems(): MutableList<ConfigItem> =
        MutableList(length()) { index -> ConfigItem.fromJson(getJSONObject(index)) }

    private fun JSONArray.toCategories(): MutableList<CategoryItem> =
        MutableList(length()) { index -> CategoryItem.fromJson(getJSONObject(index)) }

    private fun JSONArray.toContacts(): MutableList<ContactItem> =
        MutableList(length()) { index -> ContactItem.fromJson(getJSONObject(index)) }

    private fun JSONArray.toContactGroups(): MutableList<ContactGroupItem> =
        MutableList(length()) { index -> ContactGroupItem.fromJson(getJSONObject(index)) }

    private fun JSONArray.toPresets(): MutableList<PresetItem> =
        MutableList(length()) { index -> PresetItem.fromJson(getJSONObject(index)) }

    private fun List<ConfigItem>.toConfigJsonArray(): JSONArray = JSONArray().also { array ->
        forEach { item -> array.put(item.toJson()) }
    }

    private fun List<CategoryItem>.toCategoryJsonArray(): JSONArray = JSONArray().also { array ->
        forEach { item -> array.put(item.toJson()) }
    }

    private fun List<ContactItem>.toContactJsonArray(): JSONArray = JSONArray().also { array ->
        forEach { contact -> array.put(contact.toJson()) }
    }

    private fun List<ContactGroupItem>.toContactGroupJsonArray(): JSONArray = JSONArray().also { array ->
        forEach { group -> array.put(group.toJson()) }
    }

    private fun List<PresetItem>.toPresetJsonArray(): JSONArray = JSONArray().also { array ->
        forEach { preset -> array.put(preset.toJson()) }
    }

    companion object {
        private const val PREFS = "trajets_config"
        private const val KEY_CONFIG = "config"
    }
}
