package com.david.todo.api.repository

import com.david.todo.api.model.entity.Todo
import com.david.todo.api.model.repository.TodoRepository
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import reactor.test.StepVerifier

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class TodoRepositoryTest {

    @Autowired
    lateinit var repository: TodoRepository

    @BeforeAll
    fun setUp() {
        insertTodo()
    }

    @Test
    @Order(1)
    fun findAll() {
        StepVerifier.create(repository.findAll())
            .expectNext(getTodo())
            .verifyComplete()
    }

    @Test
    @Order(2)
    fun findById() {
        StepVerifier.create(repository.findById(ID_1_LONG))
            .expectNext(getTodo())
            .verifyComplete()
    }

    @Test
    @Order(3)
    fun save() {
        StepVerifier.create(repository.save(Todo(null, "teste insert 2")))
            .expectNext(Todo(2, "teste insert 2"))
            .verifyComplete()
    }

    @Test
    @Order(4)
    fun update() {
        val expected = getTodoUpdate()
        StepVerifier.create(repository.save(expected))
            .expectNext(expected)
            .verifyComplete()
    }

    @Test
    @Order(5)
    fun deleteById() {
        repository.deleteById(ID_1_LONG).subscribe()
        StepVerifier.create(repository.findById(ID_1_LONG))
            .expectNextCount(0)
            .verifyComplete()
    }

    private fun getTodo() = Todo(1, "Teste insert 1")

    private fun getTodoUpdate() = Todo(1, "Teste update 2")

    private fun insertTodo() {
        repository.save(Todo(null, "Teste insert 1")).subscribe()
    }

    companion object {
        const val ID_1_LONG = 1L
    }
}