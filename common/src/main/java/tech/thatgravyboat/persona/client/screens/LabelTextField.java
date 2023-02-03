package tech.thatgravyboat.persona.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class LabelTextField extends EditBox {

    private final Font textRenderer;

    public LabelTextField(Font textRenderer, int x, int y, int width, int height, Component text) {
        super(textRenderer, x, y, width, height, text);
        this.textRenderer = textRenderer;
    }

    public LabelTextField(Font textRenderer, int x, int y, int width, int height, @Nullable EditBox copyFrom, Component text) {
        super(textRenderer, x, y, width, height, copyFrom, text);
        this.textRenderer = textRenderer;
    }

    @Override
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta) {
        if (this.isVisible()) {
            this.textRenderer.draw(matrices, getMessage(), this.x + 1, this.y - 10, 5592405);
        }
        super.renderButton(matrices, mouseX, mouseY, delta);
    }
}
