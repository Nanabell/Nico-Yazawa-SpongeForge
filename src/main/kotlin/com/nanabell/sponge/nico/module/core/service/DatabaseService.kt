package com.nanabell.sponge.nico.module.core.service

import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.core.CoreModule
import dev.morphia.Datastore
import dev.morphia.Morphia


@RegisterService
class DatabaseService : AbstractService<CoreModule>() {

    private lateinit var dataStore: Datastore

    override fun onPreEnable() {
        val config = module.getTypedConfigAdapter().nodeOrDefault
        val morphia = Morphia()

        dataStore = morphia.createDatastore(MongoClient( MongoClientURI(config.databaseUrl)), config.database)
        dataStore.ensureIndexes()
    }
}
