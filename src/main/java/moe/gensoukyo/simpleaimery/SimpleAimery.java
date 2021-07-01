package moe.gensoukyo.simpleaimery;

import moe.gensoukyo.simpleaimery.client.PlayerInput;
import moe.gensoukyo.simpleaimery.common.network.BlacklistUpdatePacketHandler;
import moe.gensoukyo.simpleaimery.common.network.ModNetworkHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

/**
 * @author ChloePrime
 */
@Mod(
        modid = SimpleAimery.MODID,
        name = SimpleAimery.NAME,
        version = SimpleAimery.VERSION,
        useMetadata = true,
        guiFactory = "moe.gensoukyo.simpleaimery.client.config.ConfigGui"
)
public class SimpleAimery {
    public static final String MODID = "simpleaimery";
    public static final String NAME = "Simple Aimery";
    public static final String VERSION = "1.1.6";

    private static Logger logger;

    public static Logger getLogger() {
        return logger;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        if (event.getSide().isClient()) {
            preInitClient();
        }
    }
    @EventHandler
    public void init(FMLInitializationEvent event) {
        BlacklistUpdatePacketHandler.registerPerm();
    }

    @SideOnly(Side.CLIENT)
    private void preInitClient() {
        PlayerInput.init();
        ModNetworkHandler.init();
    }
}
