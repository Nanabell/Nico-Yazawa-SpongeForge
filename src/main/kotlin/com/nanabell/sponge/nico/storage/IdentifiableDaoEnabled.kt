package com.nanabell.sponge.nico.storage

import com.j256.ormlite.misc.BaseDaoEnabled
import java.util.*

abstract class IdentifiableDaoEnabled<T> : BaseDaoEnabled<T, UUID>() {
    abstract var uuid: UUID
}