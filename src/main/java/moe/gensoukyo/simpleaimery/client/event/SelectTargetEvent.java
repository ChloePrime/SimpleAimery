package moe.gensoukyo.simpleaimery.client.event;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * 选择/取消选择时触发
 * @author ChloePrime
 */
public class SelectTargetEvent extends EntityEvent {
    public final boolean isCancel;
    public SelectTargetEvent(Entity entity, boolean isCancel) {
        super(entity);
        this.isCancel = isCancel;
    }
}
