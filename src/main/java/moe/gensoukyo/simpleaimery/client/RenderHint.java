package moe.gensoukyo.simpleaimery.client;

import moe.gensoukyo.simpleaimery.SimpleAimery;
import moe.gensoukyo.simpleaimery.client.event.SelectTargetEvent;
import moe.gensoukyo.simpleaimery.common.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

/**
 * 渲染锁定提示
 * @author ChloePrime
 */
@Mod.EventBusSubscriber(value = Side.CLIENT, modid = SimpleAimery.MODID)
public class RenderHint {
    private static final ResourceLocation TEXTURE
            = new ResourceLocation(SimpleAimery.MODID, "textures/point.png");
    private static final float SIZE = 1f / 4;

    private static final Minecraft GAME_INSTANCE = Minecraft.getMinecraft();

    @Deprecated
    public static void doRender(double x, double y, double z,
                                float camYaw, float camPitch) {
        GlStateManager.pushMatrix();
        {
            GlStateManager.translate(x, y, z);
            GlStateManager.glNormal3f(0, 1, 0);
            GlStateManager.rotate(-camYaw, 0, 1, 0);
            GlStateManager.rotate(camPitch, 1, 0, 0);
            GlStateManager.scale(-0.025f, 0.025f, 0.025f);

            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(
                    GlStateManager.SourceFactor.SRC_ALPHA,
                    GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE,
                    GlStateManager.DestFactor.ZERO
            );

            GAME_INSTANCE.getTextureManager().bindTexture(TEXTURE);
            Tessellator t = Tessellator.getInstance();
            BufferBuilder builder = t.getBuffer();
            builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            builder.pos(-SIZE, SIZE, 0).tex(0, 0).endVertex();
            builder.pos(-SIZE, -SIZE, 0).tex(0, 1).endVertex();
            builder.pos(SIZE, -SIZE, 0).tex(1, 0).endVertex();
            builder.pos(SIZE, SIZE, 0).tex(1, 1).endVertex();
            t.draw();

            GlStateManager.enableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.disableBlend();
        }
        GlStateManager.popMatrix();
    }

    @Deprecated
    public static void doRender(Entity entity, RenderLivingEvent<?> event) {
        doRender(event.getX(), event.getY() + entity.height, event.getZ(),
                GAME_INSTANCE.getRenderManager().playerViewY,
                GAME_INSTANCE.getRenderManager().playerViewX);
    }

    private static boolean isGlowing;
    @SubscribeEvent
    public static void onRenderName(RenderLivingEvent.Post<?> event) {
        if (ModConfig.disableHighlight || !isGlowing) {
            return;
        }
        RenderProcessor.getCurrentTarget()
                .filter(entity -> entity.equals(event.getEntity()))
                .ifPresent(entity -> entity.setGlowing(true));
    }

    @SubscribeEvent
    public static void onSelect(SelectTargetEvent event) {
        isGlowing = !event.isCancel;
        if (!isGlowing) {
            RenderProcessor.getCurrentTarget().ifPresent(
                    entity -> entity.setGlowing(false));
        }
    }
}
