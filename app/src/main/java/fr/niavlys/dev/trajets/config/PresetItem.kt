package fr.niavlys.dev.trajets.config

import org.json.JSONArray
import org.json.JSONObject

data class PresetItem(
    val id: String,
    var label: String,
    var blocks: MutableList<MessageBlock> = mutableListOf(),
    var order: Int = 0
) {
    fun toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("label", label)
        .put("blocks", JSONArray().also { array -> blocks.forEach { array.put(it.toJson()) } })
        .put("order", order)

    companion object {
        fun fromJson(json: JSONObject): PresetItem {
            val blocks = json.optJSONArray("blocks")?.let { array ->
                MutableList(array.length()) { index -> MessageBlock.fromJson(array.getJSONObject(index)) }
            } ?: mutableListOf(MessageBlock(BlockType.TEXT, text = json.optString("message")))
            return PresetItem(
                id = json.getString("id"),
                label = json.getString("label"),
                blocks = blocks,
                order = json.optInt("order", 0)
            )
        }
    }
}

data class MessageBlock(
    val type: BlockType,
    val itemId: String? = null,
    val text: String = "",
    val minutes: Int = 0
) {
    fun toJson(): JSONObject = JSONObject()
        .put("type", type.name)
        .put("itemId", itemId)
        .put("text", text)
        .put("minutes", minutes)
        .put("cause", text)

    companion object {
        fun fromJson(json: JSONObject): MessageBlock = MessageBlock(
            type = runCatching { BlockType.valueOf(json.getString("type")) }.getOrDefault(BlockType.TEXT),
            itemId = json.optString("itemId").ifBlank { null },
            text = json.optString("text", json.optString("cause")),
            minutes = json.optInt("minutes", 0)
        )
    }
}

enum class BlockType {
    TEXT,
    ACTION,
    TRANSPORT,
    STATION,
    CONTACT,
    DELAY
}
