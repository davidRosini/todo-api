package com.david.todo.api.controller

import com.david.todo.api.controller.request.TodoRequest
import com.david.todo.api.controller.response.TodoResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("todo")
class TodoController {

    @GetMapping(produces = [MediaType.APPLICATION_NDJSON_VALUE])
    fun findAll(): Flux<TodoResponse> {
        return Flux.just(
            TodoResponse(1, "Item 1"),
            TodoResponse(2, "Item 2"),
            TodoResponse(3, "Item 3")
        )
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): Mono<TodoResponse> {
        return Mono.just(TodoResponse(1, "Item 1"))
    }

    @PostMapping
    fun save(@RequestBody todo: Mono<TodoRequest>): Mono<TodoResponse> {
        return todo.map { TodoResponse(1, it.item) }
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: String, @RequestBody todo: Mono<TodoRequest>): Mono<TodoResponse> {
        return todo.map { TodoResponse(1, it.item) }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String) {
        // TODO implement delete todo item
    }
}