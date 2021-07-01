package moe.gensoukyo.simpleaimery.client;

import moe.gensoukyo.simpleaimery.common.config.ModConfig;
import net.minecraft.util.math.MathHelper;

/**
 * @author ChloePrime
 */
public class Interpolator {
    static long startTimeIntTick;
    static float startTimePartial;

    private static final double ATAN_FACTOR = 2 / Math.PI;

    /**
     * 输入时间，输出插值后的数值
     * 输入1  输出0.5
     * 输入+∞ 输出1
     */
    public static double curve(double x) {
        return Math.atan(x) * ATAN_FACTOR;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static float curveWithPartialOnly(float partial) {
        float dTime = ModUtil.GAME_INSTANCE.world.getTotalWorldTime() - startTimeIntTick + partial;
        return (float) curve(ModConfig.getInterpolationSpeedScaled() * dTime);
    }

    public static void refreshStartTime(float partial) {
        startTimeIntTick = ModUtil.GAME_INSTANCE.world.getTotalWorldTime();
        startTimePartial = partial;
    }

    private static double lastPartial;
    private static float lastProgress;
    public static float getProgress(float partial) {
        //防止重复调用多次刷新
        if (Math.abs(partial - lastPartial) <= Float.MIN_VALUE) {
            return lastProgress;
        }
        lastPartial = partial;
        long tickBefore = startTimeIntTick;
        float partialBefore = startTimePartial;

        refreshStartTime(partial);

        float stdProgress = (startTimeIntTick - tickBefore) + (startTimePartial - partialBefore);
        lastProgress = stdProgress * ModConfig.getInterpolationSpeedScaled();
        lastProgress = MathHelper.clamp(lastProgress,0, 1);
        return lastProgress;
    }

    public static float interpolate(float start, float end, float progress) {
        return start + (end - start) * progress;
    }
}