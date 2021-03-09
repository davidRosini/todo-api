package com.david.todo.translator

import com.david.todo.api.model.entity.Todo
import com.david.todo.api.service.dto.TodoDTO
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface TodoTODTO : Translator<Todo, TodoDTO> {

    override fun translate(data: Todo): TodoDTO
}