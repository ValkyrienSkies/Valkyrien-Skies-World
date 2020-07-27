package org.valkyrienskies.addon.world.proxy

import net.minecraftforge.fml.common.event.FMLStateEvent

open class CommonProxyWorld {
    open fun preInit(e: FMLStateEvent) {}
    open fun init(e: FMLStateEvent) {}
    open fun postInit(e: FMLStateEvent) {}
}