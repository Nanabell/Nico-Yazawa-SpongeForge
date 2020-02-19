package com.nanabell.sponge.nico.storage

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.jdbc.JdbcConnectionSource
import com.j256.ormlite.table.TableUtils
import java.sql.SQLException
import java.util.*

class Persistable<T : IdentifiableDaoEnabled<T>>(url: String, clazz: Class<T>) {

    private lateinit var dao: Dao<T, UUID>

    init {
        try {
            val source = JdbcConnectionSource(url)
            dao = DaoManager.createDao(source, clazz)

            TableUtils.createTableIfNotExists(source, clazz)
        } catch (e: SQLException) {
            e.printStackTrace() //TODO: Handle
        }
    }

    fun save(obj: T) {
        if (obj.dao == null)
            obj.dao = dao

        dao.createOrUpdate(obj)
    }

    fun delete(obj: T) {
        dao.delete(obj)
    }

    fun exists(id: UUID): Boolean {
        return dao.idExists(id)
    }

    operator fun get(id: UUID): T? {
        return dao.queryForId(id)
    }

    fun getOrCreate(id: UUID): T {
        return try {
            var obj = get(id)
            if (obj == null) {
                obj = dao.dataClass.newInstance()
                obj.uuid = id

                save(obj)
            }
            obj!!
        } catch (e: Exception) {
            throw IllegalStateException("Unable to construct an instance of " + this[id])
        }
    }

    val daoClass: Class<T>
        get() = dao.dataClass

}
