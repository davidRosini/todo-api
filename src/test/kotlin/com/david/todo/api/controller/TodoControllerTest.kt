package com.david.todo.api.controller

import com.david.todo.api.controller.response.TodoResponse
import com.david.todo.helper.logger
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList

@WebFluxTest
class TodoControllerTest {

    private lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun setUp() {
        webTestClient = WebTestClient
            .bindToController(TodoController())
            .configureClient()
            .baseUrl(TODO_BASE_URI)
            .build()
    }

    @Test
    fun testGetAll() {
        webTestClient.get()
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus().isOk
            .expectBodyList<TodoResponse>()
            .consumeWith<WebTestClient.ListBodySpec<TodoResponse>> {
                LOG.info("== Response of todo itens: {} ==", it.responseBody)
                it.responseBody?.containsAll(getTodoList())?.let { list -> assert(list) }
            }
    }

    @Test
    fun testGetById() {
        webTestClient.get()
            .uri(URI_ID_1)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus().isOk
            .expectBody<TodoResponse>()
            .consumeWith {
                LOG.info("== Response of todo item: {} ==", it.responseBody)
                assert(getTodo() == it.responseBody)
            }
    }

    companion object {
        val LOG by logger()

        const val TODO_BASE_URI = "/todo"
        const val URI_ID_1 = "/1"

        fun getTodoList() = listOf(
            TodoResponse(1, "Item 1"),
            TodoResponse(2, "Item 2"),
            TodoResponse(3, "Item 3")
        )

        fun getTodo() = TodoResponse(1, "Item 1")
    }
}