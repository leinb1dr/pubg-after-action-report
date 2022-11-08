package com.leinb1dr.pubg.afteractionreport.util

import com.leinb1dr.pubg.afteractionreport.core.PubgWrapper
import io.mockk.every
import io.mockk.mockkClass
import org.springframework.http.ResponseEntity
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.toEntity
import reactor.core.publisher.Mono
import java.util.function.Consumer

class SetupWebClientMock private constructor(
    val webClient: WebClient?,
    val headerSpec: WebClient.RequestHeadersSpec<*>?,
    val responseSpec: WebClient.ResponseSpec?,
    val responseEntity: ResponseEntity<PubgWrapper>?
) {

    data class Builder(
        var webClient: WebClient,
        var headerSpec: WebClient.RequestHeadersUriSpec<*>? = null,
        var responseSpec: WebClient.ResponseSpec? = null,
        var responseEntity: ResponseEntity<PubgWrapper>? = null,
        var requestBodySpec: WebClient.RequestHeadersUriSpec<*>? = null
    ) : UriStep, RetrieveStep, EntityStep, BodyStep, BuildStep {

        fun get(): UriStep = apply {
            headerSpec = mockkClass(WebClient.RequestHeadersUriSpec::class)
            every { webClient.get() } returns headerSpec!!
        }

        override fun uri(consumer: Consumer<WebClient.RequestHeadersUriSpec<*>>): RetrieveStep = apply {
            consumer.accept(headerSpec!!)
        }

        override fun retrieve(): EntityStep = apply {
            responseSpec = mockkClass(WebClient.ResponseSpec::class)
            every { headerSpec!!.retrieve() } returns responseSpec!!
        }

        override fun toEntity(): BodyStep = apply {
            responseEntity = mockkClass(ResponseEntity::class) as ResponseEntity<PubgWrapper>
            every { responseSpec!!.toEntity<PubgWrapper>() } returns Mono.just(responseEntity!!)
        }

        override fun body(body: PubgWrapper?): BuildStep = apply {
            every { responseEntity!!.body } returns body
        }

        override fun build() = SetupWebClientMock(webClient, headerSpec, responseSpec, responseEntity)

    }

    interface UriStep {
        fun uri(consumer: Consumer<WebClient.RequestHeadersUriSpec<*>>): RetrieveStep
    }

    interface RetrieveStep {
        fun retrieve(): EntityStep
    }

    interface EntityStep {
        fun toEntity(): BodyStep
    }

    interface BodyStep {
        fun body(body: PubgWrapper?): BuildStep
    }

    interface BuildStep {
        fun build(): SetupWebClientMock
    }
}