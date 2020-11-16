package de.espirit.sfcc.publication

import de.espirit.firstspirit.access.Language
import de.espirit.firstspirit.access.project.Resolution
import de.espirit.firstspirit.access.project.TemplateSet
import de.espirit.firstspirit.access.store.ContentProducer
import de.espirit.firstspirit.access.store.PageParams
import de.espirit.firstspirit.access.store.mediastore.Media
import de.espirit.firstspirit.generate.UrlFactory

class SimpleMediaUrlFactory() : UrlFactory {

    override fun getUrl(media: Media, language: Language?, resolution: Resolution?) =
        """${media.filename}${language?.filenameSuffix() ?: ""}${resolution?.filenameSuffix() ?: ""}""".trim('/')

    private fun Resolution.filenameSuffix() = """_${this.uid}"""
    private fun Language.filenameSuffix() = """_${this.abbreviation}"""

    override fun getUrl(p0: ContentProducer?, p1: TemplateSet?, p2: Language?, p3: PageParams?): String {
        TODO("Not yet implemented")
    }
}
