package moe.gensoukyo.simpleaimery.client;

import moe.gensoukyo.simpleaimery.common.config.ModConfig;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;

/**
 * 锁定目标的限制
 * @author Administrator
 */
public class LockTargetLimitation {
    private static final double SQR_MAX_DISTANCE = 16 * 16;

    /**
     * 判断一个实体是否可以被选中
     * @param entity 被判断实体
     * @return 是否可以被锁定
     */
    public static boolean isValidTarget(EntityLivingBase entity) {

        EntityPlayerSP player = ModUtil.GAME_INSTANCE.player;
        //射线追踪之前该实体是否为可选目标
        boolean beforeRayTrace = (entity != player)
                && canEntitySeePlayer(entity, player)
                //可被攻击的实体才算
                && (!entity.isEntityInvulnerable(DamageSource.causePlayerDamage(player)))
                && (ModUtil.GAME_INSTANCE.player.getDistanceSq(entity) < SQR_MAX_DISTANCE)
                && (ModUtil.getDeltaAngle(entity) < Math.toRadians(ModConfig.maxDeltaAngle));
        if (!beforeRayTrace) {
            return false;
        }

        RayTraceResult result = ModUtil.GAME_INSTANCE.world.rayTraceBlocks(
                ModUtil.GAME_INSTANCE.player.getPositionEyes(1),
                entity.getPositionEyes(1), false,
                true, false);
        return (result == null) || (result.typeOfHit == RayTraceResult.Type.MISS);
    }

    private static boolean canEntitySeePlayer(Entity thiz, EntityPlayer player) {
        return !thiz.isInvisibleToPlayer(player);
    }
}
