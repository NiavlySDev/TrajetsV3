package fr.niavlys.dev.trajets.config

data class AppConfig(
    val transports: MutableList<ConfigItem> = mutableListOf(),
    val stations: MutableList<ConfigItem> = mutableListOf(),
    val contacts: MutableList<ContactItem> = mutableListOf()
)
