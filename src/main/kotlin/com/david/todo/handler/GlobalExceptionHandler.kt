package com.david.todo.handler

import com.david.todo.exception.ExceptionResponse
import com.david.todo.helper.logger
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.MethodNotAllowedException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
@Order(-2)
class GlobalExceptionHandler : ErrorWebExceptionHandler {

    companion object {
        val LOG by logger()
        const val UNHANDLED_CODE: Int = 9999
    }

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        LOG.error("Unhandled exception occurred at [{}] com user-agent [{}]", getPath(exchange), getUserAgent(exchange), ex)
        if (ex is MethodNotAllowedException) {
            return unhandledMethodNotAllowedExceptionResponse(exchange, ex)
        }

        if (ex is ResponseStatusException) {
            return unhandledStatusExceptionResponse(exchange, ex)
        }
        return unhandledExceptionResponse(exchange, ex)
    }

    private fun unhandledMethodNotAllowedExceptionResponse(exchange: ServerWebExchange, ex: MethodNotAllowedException): Mono<Void> {
        exchange.response.statusCode = ex.status
        exchange.response.headers.contentType = MediaType.APPLICATION_JSON
        return writeResponse(exchange, ExceptionResponse(UNHANDLED_CODE, ex.javaClass.name, ex.localizedMessage))
    }

    private fun unhandledStatusExceptionResponse(exchange: ServerWebExchange, ex: ResponseStatusException): Mono<Void> {
        exchange.response.statusCode = ex.status
        exchange.response.headers.contentType = MediaType.APPLICATION_JSON
        return writeResponse(exchange, ExceptionResponse(UNHANDLED_CODE, ex.javaClass.name, ex.localizedMessage))
    }

    private fun unhandledExceptionResponse(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        exchange.response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
        exchange.response.headers.contentType = MediaType.APPLICATION_JSON
        return writeResponse(exchange, ExceptionResponse(UNHANDLED_CODE, ex.javaClass.name, ex.localizedMessage))
    }

    private fun writeResponse(exchange: ServerWebExchange, responseBody: ExceptionResponse): Mono<Void> {
        val bufferFactory = exchange.response.bufferFactory()
        return Mono.fromCallable { Json.encodeToString(responseBody).toByteArray() }
            .flatMap { byteArray -> Mono.just(bufferFactory.wrap(byteArray)) }
            .flatMap { dataBuffer -> exchange.response.writeWith(Mono.just(dataBuffer)) }
    }

    private fun getPath(exchange: ServerWebExchange): String {
        return exchange.request.path.value()
    }

    private fun getUserAgent(exchange: ServerWebExchange): String? {
        return exchange.request.headers.getFirst(HttpHeaders.USER_AGENT)
    }
}