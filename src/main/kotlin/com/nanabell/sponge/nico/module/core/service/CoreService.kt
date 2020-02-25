package com.nanabell.sponge.nico.module.core.service

import com.nanabell.sponge.nico.internal.annotation.ApiService
import com.nanabell.sponge.nico.internal.annotation.RegisterService
import com.nanabell.sponge.nico.internal.service.AbstractService
import com.nanabell.sponge.nico.module.core.CoreModule
import com.nanabell.sponge.nico.module.core.`interface`.CoreServiceInterface

@ApiService
@RegisterService(CoreServiceInterface::class)
class CoreService : AbstractService<CoreModule>(), CoreServiceInterface {

    override fun onEnable() {
        logger.info("Hello World! StartupError: ${module.getTypedConfigAdapter().nodeOrDefault.startupError}")
    }
}