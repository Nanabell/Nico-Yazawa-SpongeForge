package com.nanabell.sponge.nico.module.quest.task

import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable

@ConfigSerializable
abstract class Task {

    open fun isComplete(): Boolean {
        return true
    }

    open fun reset() {
    }

    abstract class Builder<T : Task, B : Builder<T, B>> {

        abstract fun build(): T

        abstract fun getThis(): B
    }

}

