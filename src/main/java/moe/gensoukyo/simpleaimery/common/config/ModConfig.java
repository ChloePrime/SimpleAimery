package moe.gensoukyo.simpleaimery.common.config;

import moe.gensoukyo.simpleaimery.SimpleAimery;
import moe.gensoukyo.simpleaimery.common.network.BlacklistUpdatePacket;
import moe.gensoukyo.simpleaimery.common.network.ModNetworkHandler;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * 配置文件
 * @author ChloePrime
 */
@Config(modid = SimpleAimery.MODID)
@Mod.EventBusSubscriber(modid = SimpleAimery.MODID)
public class ModConfig {
    /**
     * simpleaimery.config.
     */
    private static final String CONFIG_KEY_HEADER = SimpleAimery.MODID + ".config.";
    /**
     * 最大锁定角度差
     * 如果角度差大于这个值则不是可选的锁定目标
     * simpleaimery.config.maxDeltaAngle
     */
    @Config.Comment("Max delta angle that can be considered as a valid target")
    @Config.LangKey(CONFIG_KEY_HEADER + "maxDeltaAngle")
    @Config.RangeDouble(min = 0.0, max = 180.0d)
    public static float maxDeltaAngle = 60;

    /**
     * 角度差小于这个值的时候按距离排序
     * 否则按角度排序
     * simpleaimery.config.sortMethodSep
     */
    @Config.Comment("Separation between comparing distance or angle\n" +
            " when sorting target priority")
    @Config.LangKey(CONFIG_KEY_HEADER + "sortMethodSep")
    @Config.RangeDouble(min = 0.0, max = 180.0)
    public static float sortMethodSep = 15;

    /**
     * 动画速度
     * 反正切曲线的x轴缩放倍率（×100）
     * simpleaimery.config.interpolationSpeed
     */
    @Config.Comment("Smooth animation speed")
    @Config.LangKey(CONFIG_KEY_HEADER + "interpolationSpeed")
    @Config.RangeDouble(min = 1e-3, max = 1e4)
    public static float interpolationSpeed = 5;

    public static float getInterpolationSpeedScaled() {
        return 0.1f * interpolationSpeed;
    }

    /**
     * simpleaimery.config.thirdPersonOffset
     */
    @Config.Comment("Third person offset along screen x")
    @Config.LangKey(CONFIG_KEY_HEADER + "thirdPersonOffset")
    @Config.RangeDouble(min = 0, max = 16)
    public static float thirdPersonOffset = 1.5f;

    @Config.Comment("Disable highlight of the target when locking at it")
    @Config.LangKey(CONFIG_KEY_HEADER + "disableHighlight")
    public static boolean disableHighlight = false;

    /**
     * simpleaimery.config.blacklist
     */
    @Config.Comment("Items that will disable this mod when holding in mainhand")
    @Config.LangKey(CONFIG_KEY_HEADER + "blacklist")
    public static String[] blacklist = {"minecraft:bow"};

    /**
     * simpleaimery.config.blacklistAsWhitelist
     */
    @Config.Comment("Blacklist Is Actually Whitelist")
    @Config.LangKey(CONFIG_KEY_HEADER + "blacklistAsWhitelist")
    public static boolean blacklistAsWhitelist = false;

    @SubscribeEvent
    public static void onCfgSync(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (SimpleAimery.MODID.equals(event.getModID())) {
            ConfigManager.sync(SimpleAimery.MODID, Config.Type.INSTANCE);
            ModNetworkHandler.WRAPPER.sendToServer(
                    new BlacklistUpdatePacket(blacklistAsWhitelist, blacklist)
            );
        }
    }
}