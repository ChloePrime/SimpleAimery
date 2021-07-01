package moe.gensoukyo.simpleaimery.common.network;

import moe.gensoukyo.simpleaimery.SimpleAimery;
import moe.gensoukyo.simpleaimery.common.config.ModConfig;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

/**
 * @author ChloePrime
 */
public class BlacklistUpdatePacketHandler {
    private static final String PERM_NODE = SimpleAimery.MODID + ".changeList";
    public static void registerPerm() {
        PermissionAPI.registerNode(PERM_NODE, DefaultPermissionLevel.OP, "");
    }

    public static class CtS implements IMessageHandler<BlacklistUpdatePacket, IMessage> {
        @Override
        public IMessage onMessage(BlacklistUpdatePacket message, MessageContext ctx) {
            ctx.getServerHandler().player.mcServer.addScheduledTask(() -> {
                if (PermissionAPI.hasPermission(ctx.getServerHandler().player, PERM_NODE)) {
                    syncCfg(message);
                    ModNetworkHandler.WRAPPER.sendToAll(message);
                }
            });
            return null;
        }
    }
    public static class StC implements IMessageHandler<BlacklistUpdatePacket, IMessage> {
        @Override
        public IMessage onMessage(BlacklistUpdatePacket message, MessageContext ctx) {
            syncCfg(message);
            return null;
        }
    }

    private static void syncCfg(BlacklistUpdatePacket packet) {
        ModConfig.blacklist = packet.list;
        ModConfig.blacklistAsWhitelist = packet.isWhite;
        ConfigManager.sync(SimpleAimery.MODID, Config.Type.INSTANCE);
    }
}