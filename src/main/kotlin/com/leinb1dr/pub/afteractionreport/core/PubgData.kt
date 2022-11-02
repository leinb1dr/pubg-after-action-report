package com.leinb1dr.pub.afteractionreport.core

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

data class PubgData(
    val type: String,
    val id: String,
    @JsonDeserialize(using = PubgAttributeDeserializer::class) val attributes: PubgAttributes?=null,
    val relationships: Map<String, PubgWrapper>?=null
)