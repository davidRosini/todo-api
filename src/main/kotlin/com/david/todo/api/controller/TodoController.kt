package com.david.todo.api.controller

import com.david.todo.api.controller.request.TodoRequest
import com.david.todo.api.controller.response.TodoResponse
import com.david.todo.api.service.TodoService
import com.david.todo.helper.logger
import com.david.todo.translator.TodoDTOToResponse
import com.david.todo.translator.TodoRequestToDTO
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
    val translatorToResponse: TodoDTOToResponse,
    val translatorToDTO: TodoRequestToDTO,
) {

    companion object {
        val LOG by logger()
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_NDJSON_VALUE, MediaType.TEXT_EVENT_STREAM_VALUE])
    fun findAll(): Flux<TodoResponse> {
        LOG.info("== Calling service to find all todo items ==")
        return service.findAll()
            .map { t -> translatorToResponse.translate(t) }
    }

    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findById(@PathVariable id: Long): Mono<TodoResponse> {
        LOG.info("== Calling service to find a todo by id=[$id] ==")
        return service.findById(id)
                .map { t -> translatorToResponse.translate(t) }
    }

    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun save(
        uriComponentsBuilder: UriComponentsBuilder,
        @RequestBody todo: Mono<TodoRequest>
    ): Mono<ResponseEntity<TodoResponse>> {
        LOG.info("== Calling service to create a new todo ==")
        return todo.flatMap { t -> service.save(translatorToDTO.translate(t)) }
                .map { t -> translatorToResponse.translate(t) }
                .doOnNext { t -> LOG.info("== Todo created response=[$t] ==") }
                .flatMap { t ->
                    Mono.just(ResponseEntity.created(uriComponentsBuilder.path("/{id}")
                            .buildAndExpand(t.id)
                            .toUri())
                            .body(t)
            )
        }
    }

    @PutMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun update(@PathVariable id: Long, @RequestBody todo: Mono<TodoRequest>): Mono<TodoResponse> {
        LOG.info("== Calling service to update a todo by id=[$id] ==")
        return todo.flatMap { t -> service.update(id, translatorToDTO.translate(t)) }
                .map { t -> translatorToResponse.translate(t) }
                .doOnNext { t -> LOG.info("== Todo updated response=[$t] ==") }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long): Mono<Void> {
        LOG.info("== Calling service to delete a todo by id=[$id] ==")
        return service.delete(id)
    }
}