package com.example.movix.data.remote.model

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "response")
data class WebshareSaltResponse(
    @param:PropertyElement(name = "status") val status: String,
    @param:PropertyElement(name = "salt") val salt: String? = null,
    @param:PropertyElement(name = "message") val message: String? = null
)

@Xml(name = "response")
data class WebshareLoginResponse(
    @param:PropertyElement(name = "status") val status: String,
    @param:PropertyElement(name = "token") val token: String? = null,
    @param:PropertyElement(name = "message") val message: String? = null
)

@Xml(name = "response")
data class WebshareSearchResponse(
    @param:PropertyElement(name = "status") val status: String,
    @param:Element(name = "file") val files: List<WebshareFile>? = null
)

@Xml(name = "file")
data class WebshareFile(
    @param:PropertyElement(name = "ident") val ident: String,
    @param:PropertyElement(name = "name") val name: String,
    @param:PropertyElement(name = "size") val size: Long? = null
)

@Xml(name = "response")
data class WebshareLinkResponse(
    @param:PropertyElement(name = "status") val status: String,
    @param:PropertyElement(name = "link") val link: String? = null,
    @param:PropertyElement(name = "message") val message: String? = null
)
