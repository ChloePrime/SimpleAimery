package moe.gensoukyo.simpleaimery.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import javax.vecmath.Vector3d;

/**
 * @author ChloePrime
 */
public class ModUtil {
    public static final Minecraft GAME_INSTANCE = Minecraft.getMinecraft();

    public static double getDeltaAngle(Entity target) {
        EntityPlayer player = GAME_INSTANCE.player;
        Vec3d startPos = player.getEntityBoundingBox().getCenter();
        Vec3d targetPos = target.getEntityBoundingBox().getCenter();
        Vec3d offset = targetPos.subtract(startPos);
        Vec3d look = player.getLookVec();

        Vector3d offsetVec = new Vector3d(offset.x, offset.y, offset.z);
        Vector3d playerLook = new Vector3d(look.x, look.y, look.z);
        return offsetVec.angle(playerLook);
    }

    /**
     * 立方体矢量追踪
     * @param start 起点
     * @param path 待追踪的位移矢量
     * @param initialDistance 初始距离
     * @return 离最近的接触点的距离
     */
    @SuppressWarnings("AlibabaUndefineMagicConstant")
    public static float cubeRaytrace(Vec3d start, Vec3d path, double initialDistance) {
        double result = initialDistance;
        for (int i = 0; i < 8; ++i) {
            double dx = ((i & 1) * 2 - 1) * 0.1;
            double dy = ((i >> 1 & 1) * 2 - 1) * 0.1;
            double dz = ((i >> 2 & 1) * 2 - 1) * 0.1;
            Vec3d start2 = start.addVector(dx, dy, dz);
            RayTraceResult raytraceresult = GAME_INSTANCE.world
                    .rayTraceBlocks(start2, start2.add(path), false,
                            true,
                            false);

            if (raytraceresult != null) {
                double d7 = raytraceresult.hitVec.distanceTo(start2);

                if (d7 < result) {
                    result = d7;
                }
            }
        }
        return (float) result;
    }

    public static Vec3d getSmoothCenter(Entity entity, float partial) {
        return entity.getPositionEyes(partial)
                .addVector(0, entity.height / 2 - entity.getEyeHeight(), 0);
    }
}
