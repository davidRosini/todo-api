package com.david.todo.api.model.repository

import com.david.todo.api.model.entity.Todo
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface TodoRepository : ReactiveCrudRepository<Todo, Long>