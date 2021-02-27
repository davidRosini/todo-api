package com.david.todo.api.controller

import com.david.todo.api.controller.request.TodoRequest
import com.david.todo.api.controller.response.TodoResponse
import com.david.todo.api.service.TodoService
import com.david.todo.helper.logger
import com.david.todo.translator.TodoControllerTranslator
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("todo")
class TodoController(
    val service: TodoService,
    val translator: TodoControllerTranslator
) {

    companion object {
        val LOG by logger()
    }

    @GetMapping
    fun findAll(): Flux<TodoResponse> {
        LOG.info("== Calling service to find all todo items ==")
        return service.findAll()
            .map { translator.translate(it) }
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): Mono<TodoResponse> {
        LOG.info("== Calling service to find a todo by id=[$id] ==")
        return service.findById(id)
                .map { translator.translate(it) }
    }

    @PostMapping
    fun save(
        uriComponentsBuilder: UriComponentsBuilder,
        @RequestBody todo: Mono<TodoRequest>
    ): Mono<ResponseEntity<TodoResponse>> {
        LOG.info("== Calling service to create a new todo ==")
        return todo.flatMap { t -> service.save(translator.translate(t)) }
                .map { t -> translator.translate(t) }
                .doOnNext { t -> LOG.info("== Todo created response=[$t] ==") }
                .flatMap { t ->
                    Mono.just(ResponseEntity.created(uriComponentsBuilder.path("/{id}")
                            .buildAndExpand(t.id)
                            .toUri())
                            .body(t)
            )
        }
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody todo: Mono<TodoRequest>): Mono<TodoResponse> {
        LOG.info("== Calling service to update a todo by id=[$id] ==")
        return todo.flatMap { t -> service.update(id, translator.translate(t)) }
                .map { t -> translator.translate(t) }
                .doOnNext { t -> LOG.info("== Todo updated response=[$t] ==") }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) {
        LOG.info("== Calling service to delete a todo by id=[$id] ==")
        service.delete(id)
    }
}