package tech.thatgravyboat.persona.client.screens.interactions;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import tech.thatgravyboat.persona.Personas;

public class ChangeModeWidget extends Button {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(Personas.MOD_ID, "textures/interactions.png");

    private Mode mode = Mode.CHAT;

    public ChangeModeWidget(int x, int y, int width, int height) {
        super(x, y, width, height, CommonComponents.EMPTY, p -> {});
    }

    @Override
    public void onPress() {
        this.mode = this.mode.equals(Mode.CHAT) ? Mode.COMMAND : Mode.CHAT;
    }

    @Override
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int v = this.isHoveredOrFocused() ? 11 : 0;
        blit(matrices, this.x, this.y, mode.u, mode.v + v, this.width, this.height, 512, 512);
    }

    public Mode getMode() {
        return this.mode;
    }

    public enum Mode {
        CHAT(252, 0),
        COMMAND(263, 0);

        public final int u;
        public final int v;

        Mode(int u, int v) {
            this.u = u;
            this.v = v;
        }
    }
}
