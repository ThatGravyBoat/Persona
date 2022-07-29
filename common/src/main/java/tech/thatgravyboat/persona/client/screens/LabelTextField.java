package tech.thatgravyboat.persona.client.screens;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

public class LabelTextField extends TextFieldWidget {

    private final TextRenderer textRenderer;

    public LabelTextField(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        super(textRenderer, x, y, width, height, text);
        this.textRenderer = textRenderer;
    }

    public LabelTextField(TextRenderer textRenderer, int x, int y, int width, int height, @Nullable TextFieldWidget copyFrom, Text text) {
        super(textRenderer, x, y, width, height, copyFrom, text);
        this.textRenderer = textRenderer;
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.isVisible()) {
            this.textRenderer.draw(matrices, getMessage(), this.x + 1, this.y - 10, 5592405);
        }
        super.renderButton(matrices, mouseX, mouseY, delta);
    }
}
