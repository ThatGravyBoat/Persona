package tech.thatgravyboat.persona.client.screens.appearance;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import tech.thatgravyboat.persona.Personas;

import java.util.function.Consumer;

public class ToggleSwitchWidget extends Button {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(Personas.MOD_ID, "textures/appearance.png");

    private boolean toggled = false;
    private final Consumer<Boolean> onPress;
    private final ToggleSwitchTooltip tooltip;

    private final int u;
    private final int v;

    public ToggleSwitchWidget(int x, int y, int u, int v, Consumer<Boolean> onPress, ToggleSwitchTooltip tooltip) {
        super(x, y, 20, 10, CommonComponents.EMPTY, p -> {});
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
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int v = this.isHoveredOrFocused() ? 10 : 0;
        int u = this.toggled ? 20 : 0;
        blit(matrices, this.x, this.y, this.u + u, this.v + v, this.width, this.height);
        if (this.isHoveredOrFocused()) {
            tooltip.apply(matrices, mouseX, mouseY, this.toggled);
        }
    }

    @FunctionalInterface
    public interface ToggleSwitchTooltip {
        void apply(PoseStack stack, int mouseX, int mouseY, boolean toggled);
    }
}
