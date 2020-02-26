package com.nanabell.sponge.nico.module.core.service

import com.nanabell.sponge.nico.internal.annotation.service.ApiService
import com.nanabell.sponge.nico.internal.annotation.service.RegisterService
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.core.CoreModule
import com.nanabell.sponge.nico.module.core.`interface`.CoreServiceInterface
import org.spongepowered.api.Sponge

@ApiService
@RegisterService(CoreServiceInterface::class)
class CoreService : AbstractService<CoreModule>(), CoreServiceInterface {

    override fun onEnable() {
        logger.info("Hello World! Api Register Status: ${Sponge.getServiceManager().isRegistered(CoreServiceInterface::class.java)}")
    }
}