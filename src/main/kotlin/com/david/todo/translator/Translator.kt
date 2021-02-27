package com.david.todo.translator

interface Translator<A, B> {
    fun translate(data: A): B
}