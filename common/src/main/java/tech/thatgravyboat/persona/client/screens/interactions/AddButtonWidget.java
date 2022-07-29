package tech.thatgravyboat.persona.client.screens.interactions;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import tech.thatgravyboat.persona.Personas;

public class AddButtonWidget extends ButtonWidget {

    private static final Identifier BACKGROUND = new Identifier(Personas.MOD_ID, "textures/interactions.png");

    public AddButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int v = !this.active ? 24 : this.isHovered() ? 12 : 0;
        drawTexture(matrices, this.x, this.y, 274, v, this.width, this.height, 512, 512);
    }
}
