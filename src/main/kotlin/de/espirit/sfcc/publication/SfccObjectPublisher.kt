package de.espirit.sfcc.publication

import de.espirit.firstspirit.access.Language
import de.espirit.firstspirit.access.project.Resolution
import de.espirit.firstspirit.access.store.mediastore.Media
import java.util.concurrent.CompletableFuture

class SfccObjectPublisher(
        private val dataApiClient: DataApiClient,
        private val mediaFolderClient: MediaFolderClient) {

    fun publishContentAsset(contentAsset: ContentAsset, siteId: String): CompletableFuture<Void> {
        return dataApiClient.putContentAsset(ContentAssetRequestParameter(siteId, contentAsset)).thenComposeWithSharedExecutor {
            assignToFolder(contentAsset, siteId)
        }
    }

    private fun assignToFolder(contentAsset: ContentAsset, siteId: String): CompletableFuture<Void> {
        val folderAssignment = ContentFolderAssignment(contentAsset.id, contentAsset.classificationFolderId, true)
        return dataApiClient.putFolderAssignment(ContentFolderAssignmentRequestParameter(siteId, folderAssignment))
    }

    fun publishSlotConfiguration(slotConfiguration: SlotConfiguration, siteId: String): CompletableFuture<Void> {
        return dataApiClient.putSlotConfiguration(SlotConfigurationRequestParameter(siteId, slotConfiguration))
    }

    fun publishMediaFile(media: Media, language: Language?, resolution: Resolution?): CompletableFuture<Void> {
        return mediaFolderClient.publishMedia(media, language, resolution)
    }
}
