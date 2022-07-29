package tech.thatgravyboat.persona.client.screens.appearance;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import tech.thatgravyboat.persona.Personas;

public class NextPageButton extends ButtonWidget {

    private static final Identifier BACKGROUND = new Identifier(Personas.MOD_ID, "textures/appearance.png");

    public NextPageButton(int x, int y, int width, int height, PressAction onPress) {
        super(x, y, width, height, new LiteralText(""), onPress);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int v = !this.active ? 168 : this.isHovered() ? 148 : 128;
        drawTexture(matrices, this.x, this.y, 140, v, this.width, this.height);
    }
}
