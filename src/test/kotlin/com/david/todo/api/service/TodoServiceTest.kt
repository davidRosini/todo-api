package com.david.todo.api.service

import com.david.todo.api.model.entity.Todo
import com.david.todo.api.model.repository.TodoRepository
import com.david.todo.api.service.dto.TodoDTO
import com.david.todo.api.service.impl.TodoServiceImpl
import com.david.todo.translator.DTOTOTodo
import com.david.todo.translator.TodoTODTO
import com.nhaarman.mockitokotlin2.any
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mapstruct.factory.Mappers
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.verify
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@WebFluxTest
@ActiveProfiles("test")
@ContextConfiguration(classes = [TodoService::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TodoServiceTest {

    private lateinit var service: TodoService

    @MockBean
    private lateinit var repository: TodoRepository

    private var translatorToDTO = Mappers.getMapper(TodoTODTO::class.java)
    private var translatorToTodo = Mappers.getMapper(DTOTOTodo::class.java)

    @BeforeAll
    fun setUp() {
        service = TodoServiceImpl(repository, translatorToDTO, translatorToTodo)
    }

    @Test
    fun testFindAll() {
        `when`(repository.findAll()).thenReturn(Flux.fromIterable(getTodoList()))

        val expected = getTodoDTOList()

        StepVerifier.create(service.findAll())
            .expectNext(expected[0], expected[1], expected[2])
            .verifyComplete()

        verify(repository, atLeastOnce()).findAll()
    }

    @Test
    fun testFindById() {
        `when`(repository.findById(ID_1_LONG)).thenReturn(Mono.just(getTodo()))

        val expected = getTodoDTO()

        StepVerifier.create(service.findById(ID_1_LONG))
            .expectNext(expected)
            .verifyComplete()

        verify(repository, atLeastOnce()).findById(anyLong())
    }

    @Test
    fun testSave() {
        `when`(repository.save(any())).thenReturn(Mono.just(getTodo()))

        val expected = getTodoDTO()

        StepVerifier.create(service.save(expected))
            .expectNext(expected)
            .verifyComplete()

        verify(repository, atLeastOnce()).save(any())
    }

    @Test
    fun testUpdate() {
        `when`(repository.findById(ID_1_LONG)).thenReturn(Mono.just(getTodo()))
        `when`(repository.save(any())).thenReturn(Mono.just(getTodoUpdate()))

        val expected = getTodoDTOUpdate()

        StepVerifier.create(service.update(ID_1_LONG, getTodoDTOUpdate()))
            .expectNext(expected)
            .verifyComplete()

        verify(repository, atLeastOnce()).findById(anyLong())
        verify(repository, atLeastOnce()).save(any())
    }

    @Test
    fun testDelete() {
        `when`(repository.findById(ID_1_LONG)).thenReturn(Mono.just(getTodo()))
        `when`(repository.deleteById(ID_1_LONG)).thenReturn(Mono.empty())

        StepVerifier.create(service.delete(ID_1_LONG))
            .verifyComplete()

        verify(repository, atLeastOnce()).findById(anyLong())
        verify(repository, atLeastOnce()).deleteById(anyLong())
    }

    @Test
    fun testFindByIdEmpty() {
        `when`(repository.findById(ID_0_LONG)).thenReturn(Mono.empty())

        StepVerifier.create(service.findById(ID_0_LONG))
            .expectError()
            .verify()

        verify(repository, atLeastOnce()).findById(anyLong())
    }

    companion object {
        const val ID_1_LONG = 1L
        const val ID_0_LONG = 0L

        fun getTodo() = Todo(1, "Item 1")
        fun getTodoUpdate() = Todo(1, "Item 1 update")

        fun getTodoList() = listOf(
            Todo(1, "Item 1"),
            Todo(2, "Item 2"),
            Todo(3, "Item 3")
        )

        fun getTodoDTO() = TodoDTO(1, "Item 1")
        fun getTodoDTOUpdate() = TodoDTO(1, "Item 1 update")

        fun getTodoDTOList() = listOf(
            TodoDTO(1, "Item 1"),
            TodoDTO(2, "Item 2"),
            TodoDTO(3, "Item 3")
        )
    }
}