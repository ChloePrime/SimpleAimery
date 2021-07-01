package moe.gensoukyo.simpleaimery.client.config;

import moe.gensoukyo.simpleaimery.SimpleAimery;
import moe.gensoukyo.simpleaimery.common.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;

import java.util.Collections;
import java.util.Set;

/**
 * 配置文件
 * @author ChloePrime
 */
public class ConfigGui implements IModGuiFactory {
    @Override
    public void initialize(Minecraft minecraftInstance) {

    }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parent) {
        return new GuiConfig(parent, ConfigElement.from(ModConfig.class).getChildElements(),
                SimpleAimery.MODID, false, false,
                SimpleAimery.class.getSimpleName() + " Config",
                "config/" + SimpleAimery.MODID + ".cfg");
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return Collections.emptySet();
    }
}
