package org.valkyrienskies.addon.world

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionType
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.EntityRegistry
import net.minecraftforge.fml.common.registry.GameRegistry
import org.apache.logging.log4j.LogManager
import org.valkyrienskies.addon.world.block.BlockValkyriumOre
import org.valkyrienskies.addon.world.capability.ICapabilityAntiGravity
import org.valkyrienskies.addon.world.capability.ImplCapabilityAntiGravity
import org.valkyrienskies.addon.world.capability.StorageAntiGravity
import org.valkyrienskies.addon.world.proxy.CommonProxyWorld
import org.valkyrienskies.addon.world.worldgen.ValkyrienSkiesWorldGen
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import java.util.*

@Mod(name = ValkyrienSkiesWorld.MOD_NAME,
    modid = ValkyrienSkiesWorld.MOD_ID,
    version = ValkyrienSkiesWorld.MOD_VERSION,
    dependencies = "required-after:" + ValkyrienSkiesMod.MOD_ID)
@EventBusSubscriber(modid = ValkyrienSkiesWorld.MOD_ID)
class ValkyrienSkiesWorld {
    lateinit var valkyriumOre: Block
    lateinit var valkyriumCrystal: Item

    @Mod.EventHandler
    private fun preInit(event: FMLPreInitializationEvent) {
        registerCapabilities()
        proxy.preInit(event)
    }

    @Mod.EventHandler
    private fun init(event: FMLInitializationEvent) {
        EntityRegistry.registerModEntity(
                ResourceLocation(MOD_ID, "fall_up_block_entity"),
                EntityFallingUpBlock::class.java,
                "fall_up_block_entity",
                75, INSTANCE, 80, 1, true)
        GameRegistry.registerWorldGenerator(ValkyrienSkiesWorldGen(), 1)
        proxy.init(event)
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        proxy.postInit(event)
    }

    private fun registerCapabilities() {
        CapabilityManager.INSTANCE.register(ICapabilityAntiGravity::class.java, StorageAntiGravity()) { ImplCapabilityAntiGravity() }
    }

    companion object {
        private val log = LogManager.getLogger(ValkyrienSkiesWorld::class.java)

        // Used for registering stuff
        val BLOCKS: MutableList<Block> = ArrayList()
        val ITEMS: MutableList<Item> = ArrayList()

        // MOD INFO CONSTANTS
        const val MOD_ID = "vs_world"
        const val MOD_NAME = "Valkyrien Skies World"
        const val MOD_VERSION = ValkyrienSkiesMod.MOD_VERSION

        // MOD INSTANCE
        @Mod.Instance(MOD_ID)
        @JvmStatic
        lateinit var INSTANCE: ValkyrienSkiesWorld

        @SidedProxy(
                clientSide = "org.valkyrienskies.addon.world.proxy.ClientProxyWorld",
                serverSide = "org.valkyrienskies.addon.world.proxy.CommonProxyWorld"
        )
        @JvmStatic
        private lateinit var proxy: CommonProxyWorld

        @CapabilityInject(ICapabilityAntiGravity::class)
        @JvmStatic
        lateinit var ANTI_GRAVITY_CAPABILITY: Capability<ICapabilityAntiGravity>

        // MOD CLASS MEMBERS
        var OREGEN_ENABLED = true

        @SubscribeEvent
        @JvmStatic
        fun registerBlocks(event: RegistryEvent.Register<Block?>) {
            log.debug("Registering blocks...")
            INSTANCE.valkyriumOre = BlockValkyriumOre()

            // Actual registering
            val blockArray = BLOCKS.toTypedArray()
            event.registry.registerAll(*blockArray)
        }

        @SubscribeEvent
        @JvmStatic
        fun registerItems(event: RegistryEvent.Register<Item?>) {
            INSTANCE.valkyriumCrystal = ItemValkyriumCrystal()
            event.registry.registerAll(*ITEMS.toTypedArray())
        }

        @SubscribeEvent
        @JvmStatic
        fun registerPotions(event: RegistryEvent.Register<Potion?>) {
            PotionInit.registerPotions(event)
        }

        @SubscribeEvent
        @JvmStatic
        fun registerPotionTypes(event: RegistryEvent.Register<PotionType?>) {
            PotionInit.registerPotionTypes(event)
        }
    }
}
