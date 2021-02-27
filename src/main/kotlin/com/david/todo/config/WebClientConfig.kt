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
class WebClientConfig {

    @Bean
    fun webClient(
        @Value("\${client.timeout.connect}") connectTimeOut: Int,
        @Value("\${client.timeout.read}") readTimeout: Long
    ): WebClient {

        val httpClient = HttpClient.create()
            .compress(true)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeOut)
            .doOnConnected { t -> t.addHandlerLast(ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS)) }

        return WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .build()
    }
}