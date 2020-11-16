package de.espirit.sfcc.publication

import com.fasterxml.jackson.annotation.JsonProperty

typealias LocalizedValue<T> = Map<String, T>
typealias SiteSpecificValue<T> = Map<String, T>
data class BodySource(val source: String)

data class ContentAsset(
        val id: String,
        @JsonProperty("classification_folder_id") val classificationFolderId: String,
        val name: LocalizedValue<String>,
        val online: SiteSpecificValue<Boolean>,
        val searchable: SiteSpecificValue<Boolean>,
        @JsonProperty("c_body") val body: LocalizedValue<BodySource>)
