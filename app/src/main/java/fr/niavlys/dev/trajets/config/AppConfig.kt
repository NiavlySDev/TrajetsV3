package fr.niavlys.dev.trajets.config

data class AppConfig(
    val transportCategories: MutableList<CategoryItem> = mutableListOf(),
    val transports: MutableList<ConfigItem> = mutableListOf(),
    val stations: MutableList<ConfigItem> = mutableListOf(),
    val contacts: MutableList<ContactItem> = mutableListOf(),
    val contactGroups: MutableList<ContactGroupItem> = mutableListOf(),
    val messagePresets: MutableList<PresetItem> = mutableListOf()
)
