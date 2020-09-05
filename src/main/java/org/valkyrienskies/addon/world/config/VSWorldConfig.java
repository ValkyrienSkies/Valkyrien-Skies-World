package org.valkyrienskies.addon.world.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.valkyrienskies.addon.world.ValkyrienSkiesWorld;
import org.valkyrienskies.mod.common.config.VSConfig;
import org.valkyrienskies.mod.common.config.VSConfigTemplate;

@SuppressWarnings("WeakerAccess") // NOTE: Any forge config option MUST be "public"
@Config(modid = ValkyrienSkiesWorld.MOD_ID)
public class VSWorldConfig extends VSConfigTemplate {

    @Config.Comment({
            "Valkyrium Crystal Anti-Gravity force.",
            "Default is 1. Set to 0 to disable."
    })
    public static double valkyriumCrystalForce = 1D;

    @Config.Comment({
            "Valkyrium Ore Anti-Gravity force.",
            "1 is the same as a crystal, default is 4.",
            "Set to 0 to disable."
    })
    public static double valkyriumOreForce = 4D;

    @Config.Comment({
            "When true valkyrium items add upwards force to players holding them",
            "When false they won't."
    })
    public static boolean valkyriumItemsLiftPlayers = true;

    /**
     * Synchronizes the data in this class and the data in the forge configuration
     */
    public static void sync() {
        ConfigManager.sync(ValkyrienSkiesWorld.MOD_ID, Config.Type.INSTANCE);

        VSConfig.onSync();
    }

    @Mod.EventBusSubscriber(modid = ValkyrienSkiesWorld.MOD_ID)
    @SuppressWarnings("unused")
    private static class EventHandler {

        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(ValkyrienSkiesWorld.MOD_ID)) {
                sync();
            }
        }
    }
}
