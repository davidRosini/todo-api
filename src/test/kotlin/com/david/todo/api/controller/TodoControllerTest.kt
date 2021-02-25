package com.david.todo.api.controller

import com.david.todo.api.controller.response.TodoResponse
import com.david.todo.api.service.TodoService
import com.david.todo.api.service.dto.TodoDTO
import com.david.todo.helper.logger
import com.david.todo.translator.TodoDTOToResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import org.mockito.Mockito.`when`
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import reactor.core.publisher.Flux

@WebFluxTest
@ActiveProfiles("test")
@ContextConfiguration(classes = [TodoService::class])
class TodoControllerTest {

    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var service: TodoService

    private var translator = Mappers.getMapper(TodoDTOToResponse::class.java)

    @BeforeEach
    fun setUp() {
        webTestClient = WebTestClient
                .bindToController(
                        TodoController(
                                service,
                                translator
                        )
                )
                .configureClient()
                .baseUrl(TODO_BASE_URI)
                .build()
    }

    @Test
    fun testGetAll() {
        `when`(service.findAll()).thenReturn(Flux.fromIterable(getTodoDTOList()))

        webTestClient.get()
                .accept(MediaType.APPLICATION_NDJSON)
                .exchange()
                .expectStatus().isOk
                .expectBodyList<TodoResponse>()
                .consumeWith<WebTestClient.ListBodySpec<TodoResponse>> {
                    LOG.info("== Response get all todo itens: {} ==", it.responseBody)
                    it.responseBody?.containsAll(getTodoResponseList())?.let { list -> assert(list) }
                }
    }

    @Test
    fun testGetById() {
        webTestClient.get()
                .uri(URI_ID_1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody<TodoResponse>()
                .consumeWith {
                    LOG.info("== Response get todo item: {} ==", it.responseBody)
                    assert(getTodo() == it.responseBody)
                }
    }

    @Test
    fun testPostTodo() {
        webTestClient.post()
                .bodyValue(getTodo())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated
                .expectBody<TodoResponse>()
                .consumeWith {
                    val todoResponse = it.responseBody
                    val location = it.responseHeaders[HttpHeaders.LOCATION]

                    LOG.info("== Header response location post todo item: {} ==", location)
                    assert(location!![0]!!.contains(todoResponse!!.id.toString()))

                    LOG.info("== Response post todo item: {} ==", todoResponse)
                    assert(getTodo() == todoResponse)
                }
    }

    @Test
    fun testPutTodo() {
        webTestClient.put()
                .uri(URI_ID_1)
                .bodyValue(getTodo())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody<TodoResponse>()
                .consumeWith {
                    LOG.info("== Response put todo item: {} ==", it.responseBody)
                    assert(getTodo() == it.responseBody)
                }
    }

    @Test
    fun testDeleteTodo() {
        webTestClient.delete()
                .uri(URI_ID_1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent
    }

    companion object {
        val LOG by logger()

        const val TODO_BASE_URI = "/todo"
        const val URI_ID_1 = "/1"

        fun getTodoResponseList() = listOf(
                TodoResponse(1, "Item 1"),
                TodoResponse(2, "Item 2"),
                TodoResponse(3, "Item 3")
        )

        fun getTodoDTOList() = listOf(
                TodoDTO(1, "Item 1"),
                TodoDTO(2, "Item 2"),
                TodoDTO(3, "Item 3")
        )

        fun getTodo() = TodoResponse(1, "Item 1")
    }
}