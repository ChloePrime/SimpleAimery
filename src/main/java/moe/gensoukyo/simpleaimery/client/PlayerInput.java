package moe.gensoukyo.simpleaimery.client;

import moe.gensoukyo.simpleaimery.SimpleAimery;
import moe.gensoukyo.simpleaimery.common.config.ModConfig;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

/**
 * @author ChloePrime
 */
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = SimpleAimery.MODID)
public class PlayerInput {
    /**
     * key.simpleaimery
     */
    private static final String CATEGORY = "key." + SimpleAimery.MODID;
    /**
     * key.simpleaimery.aim
     */
    private static final KeyBinding LOCK_KEY = new KeyBinding(
            "key." + SimpleAimery.MODID + ".aim",
            KeyConflictContext.IN_GAME,
            Keyboard.KEY_Z,
            CATEGORY
    );
    /**
     * key.simpleaimery.swapSide
     */
    private static final KeyBinding SIDE_SWAP_KEY = new KeyBinding(
            "key." + SimpleAimery.MODID + ".swapSide",
            KeyConflictContext.IN_GAME,
            Keyboard.KEY_GRAVE,
            CATEGORY
    );

    public static void init() {
        ClientRegistry.registerKeyBinding(LOCK_KEY);
        ClientRegistry.registerKeyBinding(SIDE_SWAP_KEY);
    }

    @SubscribeEvent
    public static void onKeyboardInput(InputEvent.KeyInputEvent event) {
        if (LOCK_KEY.isPressed()) {
            if (RenderProcessor.hasTarget()) {
                RenderProcessor.clearTarget();
            } else {
                chooseEntity();
            }
        }
        if (SIDE_SWAP_KEY.isPressed()) {
            RenderProcessor.tpOffsetSide *= -1;
        }
    }

    private static void chooseEntity() {
        ModUtil.GAME_INSTANCE.world.loadedEntityList.stream()
                .filter(entity -> (entity instanceof EntityLivingBase)
                        && LockTargetLimitation.isValidTarget((EntityLivingBase) entity))
                .min(PlayerInput::comparePriority)
                .ifPresent(RenderProcessor::setCurrentTarget);
    }

    private static int comparePriority(Entity e1, Entity e2) {
        double angle1 = ModUtil.getDeltaAngle(e1);
        double angle2 = ModUtil.getDeltaAngle(e2);
        double sepLine = Math.toRadians(ModConfig.sortMethodSep);
        boolean useAngle1 = angle1 >= sepLine;
        boolean useAngle2 = angle2 >= sepLine;
        if (useAngle1 || useAngle2) {
            return Double.compare(angle1, angle2);
        } else {
            double d1sq = ModUtil.GAME_INSTANCE.player.getDistanceSq(e1);
            double d2sq = ModUtil.GAME_INSTANCE.player.getDistanceSq(e2);
            return Double.compare(d1sq, d2sq);
        }
    }

}
