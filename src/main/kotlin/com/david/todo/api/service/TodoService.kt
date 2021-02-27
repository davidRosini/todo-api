package com.david.todo.api.service

import com.david.todo.api.service.dto.TodoDTO
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface TodoService {

    fun findAll(): Flux<TodoDTO>

    fun findById(id: Long): Mono<TodoDTO>

    fun save(todo: TodoDTO): Mono<TodoDTO>

    fun update(id: Long, todo: TodoDTO): Mono<TodoDTO>

    fun delete(id: Long): Mono<Void>
}