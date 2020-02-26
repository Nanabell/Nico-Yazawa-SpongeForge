package com.nanabell.sponge.nico.module.core.service

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.database.DataEntry
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.core.CoreModule
import dev.morphia.Datastore
import dev.morphia.Morphia
import dev.morphia.query.Query
import dev.morphia.query.UpdateOperations


@RegisterService
class DatabaseService : AbstractService<CoreModule>() {

    lateinit var dataStore: Datastore
        private set

    override fun onPreEnable() {
        val config = module.getConfigOrDefault()
        val morphia = Morphia()

        dataStore = morphia.createDatastore(MongoClient(MongoClientURI(config.databaseUrl)), config.database)
        dataStore.ensureIndexes()
    }

    inline fun <reified T : DataEntry> findQuery(field: String, uniqueId: Any): Query<T> {
        return dataStore.createQuery(T::class.java).field(field).equal(uniqueId)
    }

    inline fun <reified T : DataEntry> findById(field: String, uniqueId: Any): T? {
        return findQuery<T>(field, uniqueId).first()
    }

    inline fun <reified T : DataEntry> findAndDelete(field: String, uniqueId: Any): T? {
        return dataStore.findAndDelete(findQuery<T>(field, uniqueId))
    }

    inline fun <reified T : DataEntry> findAndModify(field: String, uniqueId: Any, operations: UpdateOperations<T>): T? {
        return dataStore.findAndModify(findQuery<T>(field, uniqueId), operations)
    }

    fun <T : DataEntry> save(entity: T) {
        dataStore.save(entity)
    }

    inline fun <reified T : DataEntry> newUpdateOperations(): UpdateOperations<T> {
        return dataStore.createUpdateOperations(T::class.java)
    }
}
