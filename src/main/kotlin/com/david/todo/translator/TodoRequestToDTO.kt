package com.david.todo.translator

import com.david.todo.api.controller.request.TodoRequest
import com.david.todo.api.service.dto.TodoDTO
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface TodoRequestToDTO : Translator<TodoRequest, TodoDTO> {

    @Mapping(target = "id", ignore = true)
    override fun translate(data: TodoRequest): TodoDTO
}