package com.david.todo.api.model.entity

import org.springframework.data.annotation.Id

data class Todo(
    @Id val id: Long? = null,
    var item: String
)