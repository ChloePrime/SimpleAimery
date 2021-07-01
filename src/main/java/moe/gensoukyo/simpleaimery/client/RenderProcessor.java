package moe.gensoukyo.simpleaimery.client;

import moe.gensoukyo.simpleaimery.SimpleAimery;
import moe.gensoukyo.simpleaimery.client.event.SelectTargetEvent;
import moe.gensoukyo.simpleaimery.common.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.vecmath.Tuple2f;
import javax.vecmath.Vector2f;
import java.lang.ref.WeakReference;
import java.util.Optional;

/**
 * @author ChloePrime
 */
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = SimpleAimery.MODID)
public class RenderProcessor {
    private static final Minecraft GAME_INSTANCE = Minecraft.getMinecraft();
    private static WeakReference<Entity> currentTarget = new WeakReference<>(null);

    public static Optional<Entity> getCurrentTarget() {
        return Optional.ofNullable(currentTarget.get());
    }

    public static void setCurrentTarget(Entity entity) {
        if (entity != currentTarget.get()) {
            Interpolator.refreshStartTime(1);
            currentTarget = new WeakReference<>(entity);
            MinecraftForge.EVENT_BUS.post(new SelectTargetEvent(entity, false));
        }
    }

    public static boolean hasTarget() {
        Entity ref = currentTarget.get();
        if (ref == null) {
            return false;
        }
        if (!ref.isEntityAlive()) {
            clearTarget();
            return false;
        }
        return true;
    }

    public static void clearTarget() {
        Entity ref = currentTarget.get();
        if (ref != null) {
            MinecraftForge.EVENT_BUS.post(new SelectTargetEvent(ref, true));
        }
        currentTarget.clear();
    }

    @SubscribeEvent
    public static void onPreRender(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            postRenderTick();
            return;
        }
        if (!hasTarget()) {
            return;
        }
        //锁定状态下禁止第二人称模式
        //noinspection AlibabaUndefineMagicConstant
        if (GAME_INSTANCE.gameSettings.thirdPersonView == 2) {
            GAME_INSTANCE.gameSettings.thirdPersonView = 0;
        }
        Entity target = currentTarget.get();
        assert target != null;
    }

    static Vec3d getOffsetPlayer(Entity target, float partial) {
        return ModUtil.getSmoothCenter(target, partial)
                .subtract(GAME_INSTANCE.player.getPositionEyes(partial));
    }

    @SubscribeEvent
    public static void onCamera(EntityViewRenderEvent.CameraSetup event) {
        float partialTick = (float) event.getRenderPartialTicks();
        Entity target = currentTarget.get();
        if (!hasTarget()) {
            smoothCameraOffset(0, 0, partialTick);
            return;
        }
        assert target != null;

        Vec3d offsetPlayer = getOffsetPlayer(target, partialTick);
        setPlayerRot(offsetPlayer, partialTick);

        Vec3d offsetCam;
        //noinspection AlibabaSwitchStatement
        switch (GAME_INSTANCE.gameSettings.thirdPersonView) {
            case 0:
                LAST_OFFSET.x = LAST_OFFSET.y = 0;
                offsetCam = offsetPlayer;
                break;
            case 1:
                Vec3d look = offsetPlayer.normalize();
                Vec3d camStartPos = getThirdPersonCameraCoord(partialTick, look);
                camStartPos = transformCamera(camStartPos, look, partialTick);
                offsetCam = ModUtil.getSmoothCenter(target, partialTick)
                        .subtract(camStartPos);
                break;
            default:
                return;
        }
        //改变摄像机方向
        Vector2f camRot = getYawPitch(offsetCam);
        camRot.x += 90;
        camRot.y *= -1;
        smoothRot(event.getYaw(), event.getPitch(), camRot, partialTick);
        lastCamYaw = camRot.x;
        lastCamPitch = camRot.y;
        event.setYaw(lastCamYaw);
        event.setPitch(lastCamPitch);
    }

    static float lastCamYaw;
    static float lastCamPitch;

    /**
     * 获取一个三维向量的 yaw 和 pitch
     * 单位为度
     */
    private static Vector2f getYawPitch(Vec3d mcVec3d) {
        double yaw = Math.atan2(mcVec3d.z, mcVec3d.x);
        double pitch = Math.atan2(mcVec3d.y,
                Math.sqrt(mcVec3d.x * mcVec3d.x + mcVec3d.z * mcVec3d.z));
        return new Vector2f((float) Math.toDegrees(yaw),
                (float) Math.toDegrees(pitch));
    }


    private static void smoothRot(float initYaw, float initPitch,
                                  Tuple2f target, float partial) {
        float progress = Interpolator.getProgress(partial);
        initYaw = ensureAngleNotTooBig(initYaw, target.x);
        initPitch = ensureAngleNotTooBig(initPitch, target.y);
        target.x = Interpolator.interpolate(initYaw, target.x, progress);
        target.y = Interpolator.interpolate(initPitch, target.y, progress);
    }

    @SuppressWarnings("AlibabaUndefineMagicConstant")
    private static float ensureAngleNotTooBig(float thiz, float target) {
        if ((target - thiz) > 180) {
            return thiz + 360;
        }
        if ((target - thiz) < -180) {
            return thiz - 360;
        }
        return thiz;
    }

    /**
     * 改变玩家头部方向
     */
    private static void setPlayerRot(Vec3d offset, float partial) {
        Vector2f playerRot = getYawPitch(offset);
        playerRot.x -= 90;
        playerRot.y *= -1;
        EntityPlayerSP player = GAME_INSTANCE.player;
        smoothRot(player.rotationYaw, player.rotationPitch, playerRot, partial);

        player.rotationYaw = playerRot.x;
        player.rotationPitch = playerRot.y;
        player.prevRotationYaw = player.rotationYaw;
        player.prevRotationPitch = player.rotationPitch;
        /*
        player.cameraYaw = player.rotationYaw;
        player.cameraPitch = player.rotationPitch;
        player.prevCameraYaw = player.rotationYaw;
        player.prevCameraPitch = player.rotationPitch;
        */
    }

    /**
     * 第三人称视距，原版硬编码为 4
     */
    private static final int MC_THIRD_PERSON_DISTANCE = 4;

    /**
     * 获取第三人称摄像机位置
     *
     * @see net.minecraft.client.renderer.EntityRenderer
     * 算法来自原版
     */
    private static Vec3d getThirdPersonCameraCoord(float partial, Vec3d look) {
        Vec3d start = GAME_INSTANCE.player.getPositionEyes(partial);
        //矢量追踪距离
        Vec3d path = look.scale(-MC_THIRD_PERSON_DISTANCE);
        double distance = ModUtil.cubeRaytrace(start, path, MC_THIRD_PERSON_DISTANCE);
        return start.add(look.scale(-distance));
    }

    private static final Vec3d Y_AXIS_UP = new Vec3d(0, 1, 0);
    private static final float Y_OFFSET_INIT_SIZE = 0.01f;

    private static boolean glPushed = false;

    /**
     * 第三人称左右偏移方向
     */
    static int tpOffsetSide = 1;
    private static final Vector2f LAST_OFFSET = new Vector2f();

    /**
     * 修改第三人称下的相机起始位置
     *
     * @param camBasePos 相机起始位置
     * @return 修改后的相机起始位置
     */
    private static Vec3d transformCamera(Vec3d camBasePos, Vec3d look, float partial) {
        final float offset = ModConfig.thirdPersonOffset;
        //自身坐标系下-X单位向量
        Vec3d selfMinusXIdentity = Y_AXIS_UP.crossProduct(look).normalize();
        float xOffset = ModUtil.cubeRaytrace(camBasePos, selfMinusXIdentity.scale(offset), offset);
        xOffset *= tpOffsetSide;
        Vec3d camPosAfterX = camBasePos.add(look.scale(xOffset));
        //自身坐标系下y上单位向量
        Vec3d selfYIdentity = look.crossProduct(selfMinusXIdentity);
        float yOffset = Math.abs(xOffset) / 2.5f;
        yOffset = ModUtil.cubeRaytrace(
                camPosAfterX.add(selfYIdentity.scale(Y_OFFSET_INIT_SIZE)),
                selfYIdentity.scale(yOffset), yOffset);

        smoothCameraOffset(xOffset, yOffset, partial);
        //如果加上y向量的话画面会抖动，先不加了
        return camPosAfterX;
    }

    private static void smoothCameraOffset(float x, float y, float partial) {
        float prg = Interpolator.getProgress(partial);
        LAST_OFFSET.x = Interpolator.interpolate(LAST_OFFSET.x, x, prg);
        LAST_OFFSET.y = Interpolator.interpolate(LAST_OFFSET.y, y, prg);
        moveCamera();
    }

    private static void moveCamera() {
        if (!glPushed) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(LAST_OFFSET.x, -LAST_OFFSET.y, 0);
            glPushed = true;
        }
    }

    private static void postRenderTick() {
        if (glPushed) {
            GlStateManager.popMatrix();
            glPushed = false;
        }
    }
}
