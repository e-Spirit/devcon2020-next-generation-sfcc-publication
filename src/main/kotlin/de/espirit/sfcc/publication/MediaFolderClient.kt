package de.espirit.sfcc.publication

import com.github.sardine.Sardine
import de.espirit.firstspirit.access.Language
import de.espirit.firstspirit.access.project.Resolution
import de.espirit.firstspirit.access.store.mediastore.Media
import de.espirit.firstspirit.generate.UrlFactory
import java.io.InputStream
import java.lang.IllegalStateException
import java.util.concurrent.CompletableFuture

class MediaFolderClient(private val webdavClient: Sardine, private val folderUrl: String, private val urlFactory: UrlFactory){
    fun publishMedia(media: Media, language: Language?, resolution: Resolution?): CompletableFuture<Void> {
        val relativeMediaUrl = urlFactory.getUrl(media, language, resolution)
        val inputStream = when (media.type) {
            Media.FILE -> media.getFile(language).inputStream
            Media.PICTURE -> media.getPicture(language).getInputStream(resolution)
            else -> throw IllegalStateException("Media is neither a file nor a picture.")
        }
        return putFileAsync(relativeMediaUrl, inputStream)
    }

    private fun putFileAsync(relativeFileUrl: String, inputStream: InputStream): CompletableFuture<Void> {
        return runAsyncWithSharedExecutor {
            webdavClient.put("""$folderUrl/$relativeFileUrl""", inputStream)
        }
    }
}
