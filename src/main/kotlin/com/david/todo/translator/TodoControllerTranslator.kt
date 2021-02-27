package com.david.todo.translator

import com.david.todo.api.controller.request.TodoRequest
import com.david.todo.api.controller.response.TodoResponse
import com.david.todo.api.service.dto.TodoDTO
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface TodoControllerTranslator : Translator<TodoDTO, TodoResponse> {

    override fun translate(data: TodoDTO): TodoResponse

    @Mapping(target = "id", ignore = true)
    fun translate(data: TodoRequest): TodoDTO
}