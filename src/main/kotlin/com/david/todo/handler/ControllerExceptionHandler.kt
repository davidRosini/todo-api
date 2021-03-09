package com.david.todo.handler

import com.david.todo.exception.ExceptionResponse
import com.david.todo.exception.ResourceNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ControllerExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(
        ResourceNotFoundException::class
    )
    fun handleResourceNotFoundException(ex: ResourceNotFoundException): ExceptionResponse {
        return ExceptionResponse(HttpStatus.NOT_FOUND.value(), ex.javaClass.name, ex.localizedMessage)
    }
}