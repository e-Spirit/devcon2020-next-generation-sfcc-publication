package de.espirit.sfcc.publication

import com.espirit.moddev.components.annotations.PublicComponent
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.sardine.SardineFactory
import de.espirit.common.base.Logging
import de.espirit.firstspirit.access.script.Executable
import de.espirit.firstspirit.access.store.sitestore.PageRef
import de.espirit.firstspirit.access.store.templatestore.WorkflowScriptContext
import de.espirit.firstspirit.agency.RenderingAgent
import de.espirit.sfcc.publication.ConfigurationParameter.*
import java.io.Writer
import java.net.http.HttpClient

@PublicComponent(name = "NextGenerationSfccPublication_PublicationExecutable", displayName = "Publication")
class PublicationExecutable : Executable {
    override fun execute(parameters: Map<String, Any>, out: Writer, err: Writer): Any {
        try {
            // TODO Clients should not need to be instantiated for each release
            val httpClient = HttpClient.newBuilder().executor(executor).build()
            val dataApiClient = DataApiClient(httpClient, jacksonObjectMapper(), SfccInstance("""https://${configuration.getParamValue(HOSTNAME)}"""))
            val context = parameters["context"] as WorkflowScriptContext
            val workflowElement = context.element
            if (workflowElement is PageRef) {
                val renderingAgent = context.requireSpecialist(RenderingAgent.TYPE)
                val sfccObjectPublisher = SfccObjectPublisher(dataApiClient, buildMediaPublisher())
                Publisher(renderingAgent, sfccObjectPublisher).publish(workflowElement).get()
                context.doTransition("finish_publication")
                return true
            } else {
                Logging.logError("Workflow element is not a PageRef", PublicationExecutable::class.java)
            }
        } catch (e: Exception) {
            Logging.logError("Publication failed.", e, PublicationExecutable::class.java)
        }
        return false
    }

    private fun buildMediaPublisher(): MediaFolderClient {
        val sardine = SardineFactory.begin(configuration.getParamValue(WEBDAV_USERNAME), configuration.getParamValue(WEBDAV_PASSWORD))
        sardine.enablePreemptiveAuthentication(configuration.getParamValue(HOSTNAME))
        val mediaFolderUrl = """https://${configuration.getParamValue(HOSTNAME)}/on/demandware.servlet/webdav/Sites/Libraries/${configuration.getParamValue(PUBLICATION_LIBRARY)}/default"""
        return MediaFolderClient(sardine, mediaFolderUrl, SimpleMediaUrlFactory())
    }
}
