package com.david.todo.api.service.impl

import com.david.todo.api.service.TodoService
import com.david.todo.api.service.dto.TodoDTO
import com.david.todo.helper.logger
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TodoServiceImpl : TodoService {

    companion object {
        val LOG by logger()
    }

    override fun findAll(): Flux<TodoDTO> {
        LOG.info("== Calling DAO to find all todo itens ==")
        return Flux.just(
                TodoDTO(1, "Item 1"),
                TodoDTO(2, "Item 2"),
                TodoDTO(3, "Item 3")
        )
    }

    override fun findById(id: Long): Mono<TodoDTO> {
        return Mono.just(TodoDTO(1, "Item 1"))
    }

    override fun save(todo: TodoDTO): Mono<TodoDTO> {
        return Mono.just(TodoDTO(1, todo.item))
    }
}