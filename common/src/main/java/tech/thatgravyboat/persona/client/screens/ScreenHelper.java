package tech.thatgravyboat.persona.client.screens;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.item.ItemStack;

public interface ScreenHelper {

    void renderTooltip(ItemStack stack);
    TextRenderer getText();
}
