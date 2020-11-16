package de.espirit.sfcc.publication

import de.espirit.firstspirit.access.store.pagestore.Section
import de.espirit.firstspirit.access.store.sitestore.PageRef
import de.espirit.firstspirit.agency.RenderingAgent

class SfccObjectFactory(private val renderingAgent: RenderingAgent, private val mediaListener: RenderingAgent.MediaLinkListener) {
    fun createContentAsset(section: Section<*>, pageRef: PageRef): ContentAsset {
        // TODO Render section content for each project language
        val sectionContent = renderSectionContent(section)
        return ContentAsset(
                id = computeContentAssetId(pageRef, section),
                classificationFolderId = pageRef.page.formData.get(null, "pt_sfcc_parentFolder").get() as String,
                name = mapOf("default" to pageRef.getDisplayName(null)),
                online = mapOf("default" to true),
                searchable = mapOf("default" to true),
                body = mapOf("default" to BodySource(sectionContent)))
    }

    private fun renderSectionContent(section: Section<*>): String {
        return renderingAgent.createRenderer(section)
                .urlFactory(SimpleMediaUrlFactory())
                .mediaLinkListener(mediaListener)
                .render()
                .trim()
    }

    fun createSlotConfiguration(section: Section<*>, pageRef: PageRef): SlotConfiguration {
        return SlotConfiguration(
                configurationId = """fs_slot_configuration_${section.id}""",
                context = SlotConfigurationContext.GLOBAL,
                default = true,
                enabled = true,
                slotContent = ContentAssetSlotContent(setOf(computeContentAssetId(pageRef, section))),
                slotId = section.parent.name.replace("_", "-"),
                template = section.formData.get(null, "st_sfccTemplate").get() as String,
                schedule = Schedule(null)
        )
    }

    private fun computeContentAssetId(pageRef: PageRef, section: Section<*>) =
            """fs-${pageRef.uid}-${section.id}"""
}
