package com.david.todo.api.controller

import com.david.todo.api.controller.request.TodoRequest
import com.david.todo.api.controller.response.TodoResponse
import com.david.todo.api.service.TodoService
import com.david.todo.api.service.dto.TodoDTO
import com.david.todo.helper.logger
import com.david.todo.translator.TodoDTOToResponse
import com.david.todo.translator.TodoRequestToDTO
import com.nhaarman.mockitokotlin2.any
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.`when`
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.verify
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
import reactor.core.publisher.Mono

@WebFluxTest
@ActiveProfiles("test")
@ContextConfiguration(classes = [TodoService::class])
class TodoControllerTest {

    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var service: TodoService

    private var translatorToResponse = Mappers.getMapper(TodoDTOToResponse::class.java)
    private var translatorToDTO = Mappers.getMapper(TodoRequestToDTO::class.java)

    @BeforeEach
    fun setUp() {
        webTestClient = WebTestClient
                .bindToController(
                        TodoController(
                                service,
                                translatorToResponse,
                                translatorToDTO
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

        verify(service, atLeastOnce()).findAll()
    }

    @Test
    fun testGetById() {
        `when`(service.findById(anyLong())).thenReturn(Mono.just(getTodoDTO()))

        webTestClient.get()
                .uri(URI_ID_1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody<TodoResponse>()
                .consumeWith {
                    LOG.info("== Response get todo item: {} ==", it.responseBody)
                    assert(getTodoResponse() == it.responseBody)
                }

        verify(service, atLeastOnce()).findById(anyLong())
    }

    @Test
    fun testPostTodo() {
        `when`(service.save(any())).thenReturn(Mono.just(getTodoDTO()))

        webTestClient.post()
                .bodyValue(getTodoRequest())
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
                    assert(getTodoResponse() == todoResponse)
                }

        verify(service, atLeastOnce()).save(any())
    }

    @Test
    fun testPutTodo() {
        `when`(service.update(anyLong(), any())).thenReturn(Mono.just(getTodoDTO()))

        webTestClient.put()
                .uri(URI_ID_1)
                .bodyValue(getTodoRequest())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody<TodoResponse>()
                .consumeWith {
                    LOG.info("== Response put todo item: {} ==", it.responseBody)
                    assert(getTodoResponse() == it.responseBody)
                }

        verify(service, atLeastOnce()).update(anyLong(), any())
    }

    @Test
    fun testDeleteTodo() {
        `when`(service.delete(anyLong())).thenReturn(Mono.empty())

        webTestClient.delete()
                .uri(URI_ID_1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent

        verify(service, atLeastOnce()).delete(anyLong())
    }

    companion object {
        val LOG by logger()

        const val TODO_BASE_URI = "/todo"
        const val URI_ID_1 = "/1"

        fun getTodoRequest() = TodoRequest("Item 1 request")

        fun getTodoResponse() = TodoResponse(1, "Item 1 request")

        fun getTodoResponseList() = listOf(
                TodoResponse(1, "Item 1"),
                TodoResponse(2, "Item 2"),
                TodoResponse(3, "Item 3")
        )

        fun getTodoDTO() = TodoDTO(1, "Item 1 request")

        fun getTodoDTOList() = listOf(
                TodoDTO(1, "Item 1"),
                TodoDTO(2, "Item 2"),
                TodoDTO(3, "Item 3")
        )
    }
}