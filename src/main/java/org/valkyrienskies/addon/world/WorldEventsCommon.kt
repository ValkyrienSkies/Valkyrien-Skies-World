package org.valkyrienskies.addon.world

import net.minecraft.entity.item.EntityItem
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent
import org.valkyrienskies.addon.world.block.BlockValkyriumOre
import org.valkyrienskies.addon.world.capability.AntiGravityCapabilityProvider
import org.valkyrienskies.addon.world.capability.ICapabilityAntiGravity
import org.valkyrienskies.mod.common.config.VSConfig

class WorldEventsCommon {

    @SubscribeEvent
    fun onAttachCapabilityEventItem(event: AttachCapabilitiesEvent<*>) {
        if (event.getObject() is ItemStack) {
            val stack = event.getObject() as ItemStack
            val item = stack.getItem()
            if (item is ItemValkyriumCrystal) {
                event.addCapability(
                        ResourceLocation(ValkyrienSkiesWorld.MOD_ID, "AntiGravityValue"),
                        AntiGravityCapabilityProvider(VSConfig.valkyriumCrystalForce))
            }
            if (stack.getItem() is ItemBlock) {
                val blockItem = stack.getItem() as ItemBlock
                if (blockItem.block is BlockValkyriumOre) {
                    event.addCapability(
                            ResourceLocation(ValkyrienSkiesWorld.MOD_ID, "AntiGravityValue"),
                            AntiGravityCapabilityProvider(VSConfig.valkyriumOreForce))
                }
            }
        }
    }

    @SubscribeEvent
    fun worldTick(event: WorldTickEvent) {
        if (event.phase == TickEvent.Phase.START) {
            for (entity in event.world.loadedEntityList) {
                if (entity is EntityItem) {
                    val itemEntity = entity
                    val itemStack = itemEntity.item
                    val capability = itemStack.getCapability<ICapabilityAntiGravity>(ValkyrienSkiesWorld.Companion.ANTI_GRAVITY_CAPABILITY!!, null)
                    if (capability != null) {
                        if (capability.multiplier != 0.0) {
                            val multiplier = 0.12 / capability.multiplier // trust me it multiplies Y increase
                            itemEntity.addVelocity(0.0, .1 - itemEntity.motionY * multiplier, 0.0)
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun playerTick(event: PlayerTickEvent) {
        if (event.phase == TickEvent.Phase.START) {
            val player = event.player
            //TODO: fix the fall damage
            // @thebest108: what fall damage?
            //                    --DaPorkchop_, 28/03/2017
            if (VSConfig.doValkyriumLifting && !player.isCreative) {
                for (stackArray in player.inventory.allInventories) {
                    for (stack in stackArray) {
                        if (stack != null) {
                            if (stack.getItem() is ItemBlock) {
                                val blockItem = stack.getItem() as ItemBlock
                                if (blockItem.block is BlockValkyriumOre) {
                                    player.addVelocity(0.0, .0025 * stack.stackSize * VSConfig.valkyriumOreForce, 0.0)
                                }
                            } else if (stack.getItem() is ItemValkyriumCrystal) {
                                player.addVelocity(0.0, .0025 * stack.stackSize * VSConfig.valkyriumCrystalForce, 0.0)
                            }
                        }
                    }
                }
            }
        }
    }
}