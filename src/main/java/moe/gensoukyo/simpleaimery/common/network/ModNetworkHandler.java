package moe.gensoukyo.simpleaimery.common.network;

import moe.gensoukyo.simpleaimery.SimpleAimery;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * @author ChloePrime
 */
public class ModNetworkHandler {
    public static final SimpleNetworkWrapper WRAPPER =
            NetworkRegistry.INSTANCE.newSimpleChannel(SimpleAimery.MODID);

    public static void init() {
        WRAPPER.registerMessage(
                BlacklistUpdatePacketHandler.CtS.class,
                BlacklistUpdatePacket.class,
                0, Side.SERVER);
        WRAPPER.registerMessage(
                BlacklistUpdatePacketHandler.StC.class,
                BlacklistUpdatePacket.class,
                1, Side.CLIENT
        );
    }
}
