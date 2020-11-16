package de.espirit.sfcc.publication

import de.espirit.firstspirit.access.store.pagestore.Section
import de.espirit.firstspirit.access.store.sitestore.PageRef

val PageRef.sections: List<Section<*>>
    get() = page.getChildren(Section::class.java, true).toList()

// TODO It is assumed that the site is using a private library, so that the site id and the library id are equal
val PageRef.sfccSiteId: String
    get() = configuration.getParamValue(ConfigurationParameter.PUBLICATION_LIBRARY) ?: project.name
