package com.david.todo.translator

import com.david.todo.api.controller.response.TodoResponse
import com.david.todo.api.service.dto.TodoDTO
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface TodoDTOToResponse : Translator<TodoDTO, TodoResponse> {

    override fun translate(data: TodoDTO): TodoResponse
}