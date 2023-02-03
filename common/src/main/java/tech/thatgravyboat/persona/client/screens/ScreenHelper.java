package tech.thatgravyboat.persona.client.screens;

import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;

public interface ScreenHelper {

    void renderTooltip(ItemStack stack);
    Font getText();
}
