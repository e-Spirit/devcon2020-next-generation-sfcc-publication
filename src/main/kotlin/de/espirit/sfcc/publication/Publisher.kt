package de.espirit.sfcc.publication

import de.espirit.firstspirit.access.store.sitestore.PageRef
import de.espirit.firstspirit.agency.RenderingAgent
import java.util.concurrent.CompletableFuture

class Publisher(private val renderingAgent: RenderingAgent, private val publisher: SfccObjectPublisher) {

    fun publish(pageRef: PageRef): CompletableFuture<Void> = publishAsync(pageRef)

    private fun publishAsync(pageRef: PageRef): CompletableFuture<Void> {
        val futures = mutableSetOf<CompletableFuture<Void>>()
        val sfccObjectFactory = SfccObjectFactory(renderingAgent) { media, language, resolution ->
            val mediaFuture = publisher.publishMediaFile(media, language, resolution)
            futures.add(mediaFuture)
        }

        val sections = pageRef.sections
        sections.map { sfccObjectFactory.createContentAsset(it, pageRef) }
                .map { publisher.publishContentAsset(it, pageRef.sfccSiteId) }
                .forEach { futures.add(it) }

        sections.map { sfccObjectFactory.createSlotConfiguration(it, pageRef) }
                .map { publisher.publishSlotConfiguration(it, pageRef.sfccSiteId) }
                .forEach { futures.add(it) }
        return CompletableFuture.allOf(*futures.toTypedArray())
    }
}
