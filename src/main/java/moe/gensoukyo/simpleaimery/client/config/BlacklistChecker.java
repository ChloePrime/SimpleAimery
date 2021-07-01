package moe.gensoukyo.simpleaimery.client.config;

import moe.gensoukyo.simpleaimery.SimpleAimery;
import moe.gensoukyo.simpleaimery.client.ModUtil;
import moe.gensoukyo.simpleaimery.client.RenderProcessor;
import moe.gensoukyo.simpleaimery.common.config.ModConfig;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Arrays;

/**
 * 配置文件
 * @author ChloePrime
 */
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = SimpleAimery.MODID)
public class BlacklistChecker {

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        EntityPlayerSP player = ModUtil.GAME_INSTANCE.player;
        if (player == null) {
            return;
        }
        ResourceLocation itemIdOop = player.getHeldItemMainhand()
                .getItem().getRegistryName();
        String itemId = (itemIdOop == null) ? "null" : itemIdOop.toString();
        if (RenderProcessor.hasTarget() && shouldBan(itemId)) {
            RenderProcessor.clearTarget();
        }
    }

    private static boolean shouldBan(String id) {
        return Arrays.asList(ModConfig.blacklist).contains(id)
                ^ ModConfig.blacklistAsWhitelist;
    }
}
