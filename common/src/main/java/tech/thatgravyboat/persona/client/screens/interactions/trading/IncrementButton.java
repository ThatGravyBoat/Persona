package tech.thatgravyboat.persona.client.screens.interactions.trading;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import tech.thatgravyboat.persona.Personas;

import java.util.function.Supplier;

public class IncrementButton extends ClickableWidget {

    private static final Identifier BACKGROUND = new Identifier(Personas.MOD_ID, "textures/trading.png");

    private int max = 1;
    private int min = 1;

    private int count = 1;

    private Supplier<ItemStack> callback = null;

    public IncrementButton(int x, int y) {
        super(x, y, 9, 16, new LiteralText(""));
    }

    public void setBounds(int min, int max) {
        this.max = max;
        this.min = min;
    }

    public void setCount(int count) {
        this.count = count;
        if (this.callback != null) {
            this.callback.get().setCount(this.count);
        }
    }

    public void setCallback(Supplier<ItemStack> callback) {
        this.callback = callback;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, BACKGROUND);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            int v = this.max == this.count ? 168 : isInBounds(this.x + 1, this.y + 1, mouseX, mouseY) ? 162 : 156;
            drawTexture(matrices, this.x + 1, this.y + 1, 0,v, 7, 6);
            v = this.min == this.count ? 168 : isInBounds(this.x + 1, this.y + 9, mouseX, mouseY) ? 162 : 156;
            drawTexture(matrices, this.x + 1, this.y + 9, 7, v, 7, 6);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (this.visible) {
            if (this.max != this.count && isInBounds(this.x + 1, this.y + 1, (int) mouseX, (int) mouseY)) {
                setCount(Screen.hasShiftDown() ? Math.min(this.count + 10, this.max) : this.count + 1);
            }
            if (this.min != this.count && isInBounds(this.x + 1, this.y + 9, (int) mouseX, (int) mouseY)) {
                setCount(Screen.hasShiftDown() ? Math.max(this.count - 10, this.min) : this.count - 1);
            }
        }
    }

    private boolean isInBounds(int x, int y, int mouseX, int mouseY) {
        return mouseX > x && mouseX < x + 8 && mouseY > y && mouseY < y + 7;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }
}
