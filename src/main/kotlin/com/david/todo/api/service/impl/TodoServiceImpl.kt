package com.david.todo.api.service.impl

import com.david.todo.api.model.repository.TodoRepository
import com.david.todo.api.service.TodoService
import com.david.todo.api.service.dto.TodoDTO
import com.david.todo.exception.ResourceNotFoundException
import com.david.todo.helper.logger
import com.david.todo.translator.DTOTOTodo
import com.david.todo.translator.TodoTODTO
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TodoServiceImpl(
    val repository: TodoRepository,
    val translatorToDTO: TodoTODTO,
    val translatorToTodo: DTOTOTodo
) : TodoService {

    companion object {
        val LOG by logger()
        const val TODO_ITEM_NOT_FOUND = "Todo item not found!"
    }

    override fun findAll(): Flux<TodoDTO> {
        LOG.info("== Calling DAO to find all todos ==")
        return repository.findAll()
            .map { t -> translatorToDTO.translate(t) }
    }

    override fun findById(id: Long): Mono<TodoDTO> {
        LOG.info("== Calling DAO to find todo by id=[$id] ==")
        return repository.findById(id)
            .switchIfEmpty(Mono.error(ResourceNotFoundException(TODO_ITEM_NOT_FOUND)))
            .map { t -> translatorToDTO.translate(t) }
    }

    override fun save(todo: TodoDTO): Mono<TodoDTO> {
        LOG.info("== Calling DAO to save todo=[$todo] ==")
        return repository.save(translatorToTodo.translate(todo))
            .map { t -> translatorToDTO.translate(t) }
    }

    override fun update(id: Long, todo: TodoDTO): Mono<TodoDTO> {
        LOG.info("== Calling DAO to update todo by id=[$id], todo=[$todo] ==")
        return findById(id)
            .flatMap { t -> Mono.just(translatorToTodo.translate(t)) }
            .flatMap { t -> t.item = todo.item; repository.save(t) }
            .map { t -> translatorToDTO.translate(t) }
    }

    override fun delete(id: Long): Mono<Void> {
        LOG.info("== Calling DAO to delete todo by id=[$id] ==")
        return findById(id)
            .flatMap { t -> repository.deleteById(t.id!!) }
    }
}