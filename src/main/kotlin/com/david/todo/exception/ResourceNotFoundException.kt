package com.david.todo.exception

import org.springframework.core.NestedRuntimeException

class ResourceNotFoundException(msg: String) : NestedRuntimeException(msg)