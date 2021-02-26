package com.david.todo.api.controller

import com.david.todo.api.controller.request.TodoRequest
import com.david.todo.api.controller.response.TodoResponse
import com.david.todo.api.service.TodoService
import com.david.todo.helper.logger
import com.david.todo.translator.TodoDTOTranslator
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("todo")
class TodoController(
    val service: TodoService,
    val translator: TodoDTOTranslator
) {

    companion object {
        val LOG by logger()
    }

    @GetMapping(produces = [MediaType.APPLICATION_NDJSON_VALUE])
    fun findAll(): Flux<TodoResponse> {
        LOG.info("== Calling service to find all todo itens ==")
        return service.findAll()
            .map { translator.translate(it) }
    }

    @GetMapping("/{id}")
    fun findById(@PathVariable id: Long): Mono<TodoResponse> {
        LOG.info("== Calling to find a todo by id=[$id] ==")
        return service.findById(id)
                .map { translator.translate(it) }
    }

    @PostMapping
    fun save(
        uriComponentsBuilder: UriComponentsBuilder,
        @RequestBody todo: Mono<TodoRequest>
    ): Mono<ResponseEntity<TodoResponse>> {
        LOG.info("== Calling to create a new todo ==")
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
    fun update(@PathVariable id: String, @RequestBody todo: Mono<TodoRequest>): Mono<TodoResponse> {
        LOG.info("== Calling to update a todo by id=[$id] ==")
        return todo.map { TodoResponse(1, it.item) }
            .doOnNext { t -> LOG.info("== Todo updated response=[$t] ==") }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String) {
        LOG.info("== Calling to delete a todo by id=[$id] ==")
    }
}