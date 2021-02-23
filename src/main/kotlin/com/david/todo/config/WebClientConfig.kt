package com.david.todo.config

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import java.util.concurrent.TimeUnit
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient

@Configuration
class WebClientConfig(
    @Value("\${client.timeout.connect}") val connectTimeOut: Int,
    @Value("\${client.timeout.read}") val readTimeout: Long
) {

    @Bean
    fun webClient(): WebClient {

        val httpClient = HttpClient.create()
            .compress(true)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeOut)
            .doOnConnected { t -> t.addHandlerLast(ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS)) }

        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .build()
    }
}