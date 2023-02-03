package tech.thatgravyboat.persona.client.screens.interactions;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import tech.thatgravyboat.persona.Personas;

public class ChangeListWidget extends Button {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(Personas.MOD_ID, "textures/interactions.png");

    private Mode mode = Mode.LIST;

    public ChangeListWidget(int x, int y, int width, int height, OnTooltip tooltipSupplier) {
        super(x, y, width, height, CommonComponents.EMPTY, p -> {}, tooltipSupplier);
    }

    @Override
    public void onPress() {
        this.mode = switch (this.mode) {
            case LIST -> Mode.DELAYED;
            case DELAYED -> Mode.RANDOM;
            case RANDOM -> Mode.LIST;
        };
    }

    @Override
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int v = this.isHoveredOrFocused() ? 12 : 0;
        blit(matrices, this.x, this.y, mode.u, mode.v + v, this.width, this.height, 512, 512);
        if (this.isHoveredOrFocused()) {
            renderToolTip(matrices, mouseX, mouseY);
        }
    }

    public Mode getMode() {
        return this.mode;
    }

    public enum Mode {
        DELAYED(298, 0),
        LIST(286, 0),
        RANDOM(310, 0);

        public final int u;
        public final int v;

        Mode(int u, int v) {
            this.u = u;
            this.v = v;
        }
    }
}
