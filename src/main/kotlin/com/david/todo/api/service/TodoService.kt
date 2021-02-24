package com.david.todo.api.service

import com.david.todo.api.service.dto.TodoDTO
import reactor.core.publisher.Flux

interface TodoService {

    fun findAll(): Flux<TodoDTO>
}