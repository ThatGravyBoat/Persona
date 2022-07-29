package tech.thatgravyboat.persona.client.screens.appearance;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import tech.thatgravyboat.persona.Personas;

import java.util.function.Consumer;

public class ToggleSwitchWidget extends ButtonWidget {

    private static final Identifier BACKGROUND = new Identifier(Personas.MOD_ID, "textures/appearance.png");

    private boolean toggled = false;
    private final Consumer<Boolean> onPress;
    private final ToggleSwitchTooltip tooltip;

    private final int u;
    private final int v;

    public ToggleSwitchWidget(int x, int y, int u, int v, Consumer<Boolean> onPress, ToggleSwitchTooltip tooltip) {
        super(x, y, 20, 10, new LiteralText(""), p -> {});
        this.onPress = onPress;
        this.tooltip = tooltip;
        this.u = u;
        this.v = v;
    }

    @Override
    public void onPress() {
        this.toggled = !this.toggled;
        onPress.accept(this.toggled);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int v = this.isHovered() ? 10 : 0;
        int u = this.toggled ? 20 : 0;
        drawTexture(matrices, this.x, this.y, this.u + u, this.v + v, this.width, this.height);
        if (this.isHovered()) {
            tooltip.apply(matrices, mouseX, mouseY, this.toggled);
        }
    }

    @FunctionalInterface
    public interface ToggleSwitchTooltip {
        void apply(MatrixStack stack, int mouseX, int mouseY, boolean toggled);
    }
}
