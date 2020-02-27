package com.nanabell.sponge.nico.internal.interfaces

interface Reloadable {

    @Throws(Exception::class)
    fun onReload()
}