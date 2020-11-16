package de.espirit.sfcc.publication

import java.util.*

enum class ConfigurationParameter(val configurationKey: String) {
    HOSTNAME("sfcc.hostname"),
    WEBDAV_USERNAME("sfcc.webdav.username"),
    WEBDAV_PASSWORD("sfcc.webdav.password"),
    PUBLICATION_LIBRARY("sfcc.publication.library")
}

val configuration: Properties by lazy {
    val resourceAsStream = PublicationExecutable::class.java.getResourceAsStream("configuration.properties")
    val properties = Properties()
    properties.load(resourceAsStream)
    properties
}

fun Properties.getParamValue(configParameter: ConfigurationParameter): String? = this[configParameter.configurationKey] as String?
