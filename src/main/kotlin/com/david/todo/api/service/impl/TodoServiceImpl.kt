package com.david.todo.api.service.impl

import com.david.todo.api.model.repository.TodoRepository
import com.david.todo.api.service.TodoService
import com.david.todo.api.service.dto.TodoDTO
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
    }

    override fun findAll(): Flux<TodoDTO> {
        LOG.info("== Calling DAO to find all todos ==")
        return repository.findAll()
            .map { t -> translatorToDTO.translate(t) }
    }

    override fun findById(id: Long): Mono<TodoDTO> {
        LOG.info("== Calling DAO to find todo by id=[$id] ==")
        return repository.findById(id)
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
            .flatMap { t -> t.item = todo.item; save(t) }
    }

    override fun delete(id: Long): Mono<Void> {
        LOG.info("== Calling DAO to delete todo by id=[$id] ==")
        return findById(id)
            .flatMap { t -> repository.deleteById(t.id) }
    }
}