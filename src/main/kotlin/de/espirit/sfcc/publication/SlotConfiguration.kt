package de.espirit.sfcc.publication

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

enum class SlotConfigurationContext {
    @JsonProperty("global") GLOBAL,
    @JsonProperty("category") CATEGORY,
    @JsonProperty("folder") FOLDER
}

enum class SlotContentType {
    @JsonProperty("products") PRODUCTS,
    @JsonProperty("categories") CATEGORIES,
    @JsonProperty("content_assets") CONTENT_ASSETS,
    @JsonProperty("html") HTML,
    @JsonProperty("recommended_products") RECOMMENDED_PRODUCTS
}

sealed class SlotContent(val type: SlotContentType)
data class ContentAssetSlotContent(@JsonProperty("content_asset_ids") val contentAssetIds: Set<String>) : SlotContent(SlotContentType.CONTENT_ASSETS)

data class Schedule(
        @JsonProperty("start_date")
        @JsonInclude (JsonInclude.Include.NON_NULL)
        val startDate: Date?)

data class SlotConfiguration(
        @JsonProperty("configuration_id") val configurationId: String,
        val context: SlotConfigurationContext,
        val default: Boolean,
        val enabled: Boolean,
        @JsonProperty("slot_content") val slotContent: SlotContent,
        @JsonProperty("slot_id") val slotId: String,
        val template: String,
        val schedule: Schedule
)
