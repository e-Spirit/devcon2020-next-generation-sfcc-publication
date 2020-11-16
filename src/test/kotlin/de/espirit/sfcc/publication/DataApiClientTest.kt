package de.espirit.sfcc.publication

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import java.net.http.HttpClient
import java.util.*
import kotlin.test.Test

class DataApiClientTest {

    private val sfccInstance = SfccInstance(
            authenticationUrl = "http://localhost:8080/access_token",
            baseUrl = "http://localhost:8080"
    )

    @Test fun testPutContentAsset() {
        runTestWithMockServer {
            val accessToken = stubAuthenticationRequest()
            val library = "MyLibrary"
            val contentAssetRequestUrl = """/s/-/dw/data/v19_5/libraries/$library/content/asset4711"""
            stubFor(put(urlEqualTo(contentAssetRequestUrl)).willReturn(aResponse().withStatus(201)))

            val contentAsset = ContentAsset(
                    id = "asset4711",
                    classificationFolderId = "fs-global",
                    name = mapOf("default" to "My Content Asset"),
                    online = mapOf("default" to true),
                    searchable = mapOf("default" to true),
                    body = mapOf("default" to BodySource("<h1>This is my content asset!</h1>")))

            it.putContentAsset(ContentAssetRequestParameter(library, contentAsset))

            verifyAuthenticationRequest()
            verify(putRequestedFor(urlEqualTo(contentAssetRequestUrl))
                    .withHeader("Content-Type", equalTo("application/json"))
                    .withHeader("Authorization", equalTo("""Bearer $accessToken"""))
                    .withRequestBody(equalToJson(
                            """
                            {
                                "id" : "asset4711",
                                "classification_folder_id": "fs-global",
                                "name": {
                                    "default": "My Content Asset"
                                },
                                "online": {
                                    "default": true
                                },
                                "searchable": {
                                    "default": true
                                },
                                "c_body": {
                                    "default": {            
                                        "source": "<h1>This is my content asset!</h1>"
                                    }
                                }
                            }
                        """
                    )))
        }
    }

    @Test fun testPutFolderAssignment() {
        runTestWithMockServer {
            val accessToken = stubAuthenticationRequest()
            val library = "MyLibrary"
            val contentAssetId = "asset4711"
            val contentFolderId = "fs-global"
            val folderAssignmentRequestUrl = """/s/-/dw/data/v19_5/libraries/$library/folder_assignments/$contentAssetId/$contentFolderId"""

            stubFor(put(urlEqualTo(folderAssignmentRequestUrl)).willReturn(aResponse().withStatus(201)))

            val contentFolderAssignment = ContentFolderAssignment(contentAssetId, contentFolderId, true)
            it.putFolderAssignment(ContentFolderAssignmentRequestParameter(library, contentFolderAssignment))

            verifyAuthenticationRequest()
            verify(putRequestedFor(urlEqualTo(folderAssignmentRequestUrl))
                    .withHeader("Content-Type", equalTo("application/json"))
                    .withHeader("Authorization", equalTo("""Bearer $accessToken"""))
                    .withRequestBody(equalToJson(
                            """
                            {
                               "content_id": "$contentAssetId",
                               "default": true,
                               "folder_id": "$contentFolderId"
                            }
                        """
                    )))
        }
    }

    private fun verifyAuthenticationRequest() {
        verify(postRequestedFor(urlEqualTo("/access_token"))
                .withHeader("Authorization", equalTo("Basic YWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhOmFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYWFhYQ=="))
                .withHeader("Content-Type", equalTo("application/x-www-form-urlencoded"))
                .withRequestBody(equalTo("grant_type=client_credentials")))
    }

    @Test fun testPutSlotConfiguration() {
        runTestWithMockServer {
            val accessToken = stubAuthenticationRequest()
            val slot = "home-main-m"
            val site = "MySite"
            val slotConfigRequestUrl = """/s/-/dw/data/v19_5/sites/$site/slots/$slot/slot_configurations/slotConfiguration4711"""
            stubFor(put(urlEqualTo(slotConfigRequestUrl)).willReturn(aResponse().withStatus(201)))

            val slotConfiguration = SlotConfiguration(
                    configurationId = "slotConfiguration4711",
                    context = SlotConfigurationContext.GLOBAL,
                    default = true,
                    enabled = true,
                    slotContent = ContentAssetSlotContent(setOf("home-main")),
                    slotId = slot,
                    template = "slots/content/contentAssetBody.isml",
                    schedule = Schedule(null)
            )
            it.putSlotConfiguration(SlotConfigurationRequestParameter(site, slotConfiguration))

            verifyAuthenticationRequest()
            verify(putRequestedFor(urlEqualTo(slotConfigRequestUrl))
                    .withHeader("Content-Type", equalTo("application/json"))
                    .withHeader("Authorization", equalTo("""Bearer $accessToken"""))
                    .withRequestBody(equalToJson(
                            """
                            {
                                "configuration_id": "slotConfiguration4711",
                                "context": "global",
                                "default": true,
                                "enabled": true,
                                "slot_content": {
                                    "content_asset_ids": [
                                        "home-main"
                                    ],
                                    "type": "content_assets"
                                },
                                "slot_id": "home-main-m",
                                "template": "slots/content/contentAssetBody.isml",
                                "schedule": {}
                            }
                            """
                    )))
        }
    }

    private fun runTestWithMockServer(testFunction: (dataApiClient: DataApiClient) -> Unit) {
        val wireMockServer = WireMockServer()
        wireMockServer.start()
        try {
            testFunction(DataApiClient(HttpClient.newHttpClient(), jacksonObjectMapper(), sfccInstance))
        } finally {
            wireMockServer.stop()
        }
    }

    private fun stubAuthenticationRequest(): String {
        val accessToken = """my_access_token_${Random().nextInt(100)}"""
        stubFor(post("/access_token").willReturn(aResponse().withStatus(200).withBody(
                """{ "expires_in": 1799, "token_type": "Bearer", "access_token": "$accessToken" }""")))
        return accessToken
    }
}
