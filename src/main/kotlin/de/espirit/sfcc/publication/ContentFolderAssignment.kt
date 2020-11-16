package de.espirit.sfcc.publication

import com.fasterxml.jackson.annotation.JsonProperty

data class ContentFolderAssignment(
        @JsonProperty("content_id") val contentId: String,
        @JsonProperty("folder_id") val folderId: String,
        val default: Boolean)
