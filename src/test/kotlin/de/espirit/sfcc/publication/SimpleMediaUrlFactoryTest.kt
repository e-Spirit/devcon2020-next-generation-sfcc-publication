package de.espirit.sfcc.publication

import de.espirit.firstspirit.access.Language
import de.espirit.firstspirit.access.project.Resolution
import de.espirit.firstspirit.access.store.mediastore.Media
import io.mockk.every
import io.mockk.mockk
import kotlin.test.Test
import kotlin.test.assertEquals

class SimpleMediaUrlFactoryTest {
    @Test fun testGetMediaUrlWithoutResolution() {
        val simpleMediaUrlFactory = SimpleMediaUrlFactory()
        val media = mockk<Media>()
        every { media.filename } returns "foo"

        val url = simpleMediaUrlFactory.getUrl(media, null, null)
        assertEquals("foo", url, "Unexpected url")
    }

    @Test fun testGetMediaUrlWithLanguage() {
        val simpleMediaUrlFactory = SimpleMediaUrlFactory()
        val media = mockk<Media>()
        val language = mockk<Language>()

        every { media.filename } returns "foo"
        every { language.abbreviation } returns "EN_GB"

        val url = simpleMediaUrlFactory.getUrl(media, language, null)
        assertEquals("foo_EN_GB", url, "Unexpected url")
    }

    @Test fun testGetMediaUrlWithResolution() {
        val simpleMediaUrlFactory = SimpleMediaUrlFactory()
        val media = mockk<Media>()
        val resolution = mockk<Resolution>()

        every { media.filename } returns "foo"
        every { resolution.uid } returns "MY_RESOLUTION"

        val url = simpleMediaUrlFactory.getUrl(media, null, resolution)
        assertEquals("foo_MY_RESOLUTION", url, "Unexpected url")
    }

    @Test fun testGetMediaUrlWithLanguageAndResolution() {
        val simpleMediaUrlFactory = SimpleMediaUrlFactory()
        val media = mockk<Media>()
        val language = mockk<Language>()
        val resolution = mockk<Resolution>()

        every { media.filename } returns "foo"
        every { language.abbreviation } returns "EN_GB"
        every { resolution.uid } returns "MY_RESOLUTION"

        val url = simpleMediaUrlFactory.getUrl(media, language, resolution)
        assertEquals("foo_EN_GB_MY_RESOLUTION", url, "Unexpected url")
    }
}
