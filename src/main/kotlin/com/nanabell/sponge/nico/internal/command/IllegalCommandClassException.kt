package com.nanabell.sponge.nico.internal.command

class IllegalCommandClassException : Exception {
    constructor(message: String): super(message)
    constructor(clazz: Class<out AbstractCommand<*>>): super("Implementation $clazz of AbstractCommand must be Annotated with @RegisterCommand!")
}