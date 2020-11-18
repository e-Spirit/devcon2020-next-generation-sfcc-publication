package de.espirit.sfcc.publication

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.security.AccessController
import java.security.PrivilegedAction
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

@JsonIgnoreProperties(ignoreUnknown = true)
data class AuthenticationResponse(@JsonProperty("access_token") val accessToken: String)

data class SfccInstance(
    val baseUrl: String,
    val authenticationUrl: String = "https://account.demandware.com/dw/oauth2/access_token",
    val clientId: String = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
    val clientPassword: String = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")

data class ContentAssetRequestParameter(val library: String, val contentAsset: ContentAsset)
data class SlotConfigurationRequestParameter(val site: String, val slotConfiguration: SlotConfiguration)
data class ContentFolderAssignmentRequestParameter(val library: String, val contentFolderAssignment: ContentFolderAssignment)

class DataApiClient(private val httpClient: HttpClient, private val mapper: ObjectMapper, private val sfccInstance: SfccInstance) {

    private fun requestAccessToken(): Future<AuthenticationResponse> {
        val accessTokenRequest = HttpRequest.newBuilder(URI(sfccInstance.authenticationUrl))
                .header("Authorization", """Basic ${Base64.getEncoder().encodeToString("""${sfccInstance.clientId}:${sfccInstance.clientPassword}""".toByteArray())}""")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .build()

        return httpClient.sendAsync(accessTokenRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply { it.body() }
                .thenApply { AccessController.doPrivileged(PrivilegedAction { mapper.readValue(it, AuthenticationResponse::class.java) }) }
    }

    fun putContentAsset(parameter: ContentAssetRequestParameter): CompletableFuture<Void> =
        put("""${sfccInstance.baseUrl}/s/-/dw/data/v19_5/libraries/${parameter.library}/content/${parameter.contentAsset.id}""", parameter.contentAsset)

    fun putSlotConfiguration(parameter: SlotConfigurationRequestParameter): CompletableFuture<Void> =
        put("""${sfccInstance.baseUrl}/s/-/dw/data/v19_5/sites/${parameter.site}/slots/${parameter.slotConfiguration.slotId}/slot_configurations/${parameter.slotConfiguration.configurationId}""",
                parameter.slotConfiguration)

    fun putFolderAssignment(parameter: ContentFolderAssignmentRequestParameter): CompletableFuture<Void> =
        put("""${sfccInstance.baseUrl}/s/-/dw/data/v19_5/libraries/${parameter.library}/folder_assignments/${parameter.contentFolderAssignment.contentId}/${parameter.contentFolderAssignment.folderId}""", parameter.contentFolderAssignment)

    private fun <T> put(endpoint: String, payload: T): CompletableFuture<Void> {
        val payloadAsJsonString = mapper.writeValueAsString(payload)
        val slotConfigRequest = HttpRequest.newBuilder(URI(endpoint))
                .header("Content-Type", "application/json")
                // TODO Do not request a new token on each data api request
                .header("Authorization", """Bearer ${requestAccessToken().get().accessToken}""")
                .PUT(HttpRequest.BodyPublishers.ofString(payloadAsJsonString))
                .build()
        return httpClient.sendAsync(slotConfigRequest, HttpResponse.BodyHandlers.discarding()).thenApply { it.body() }
    }
}
