package tech.thatgravyboat.persona.client.screens.interactions;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import tech.thatgravyboat.persona.Personas;

public class ChangeListWidget extends ButtonWidget {

    private static final Identifier BACKGROUND = new Identifier(Personas.MOD_ID, "textures/interactions.png");

    private Mode mode = Mode.LIST;

    public ChangeListWidget(int x, int y, int width, int height, TooltipSupplier tooltipSupplier) {
        super(x, y, width, height, new LiteralText(""), p -> {}, tooltipSupplier);
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
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int v = this.isHovered() ? 12 : 0;
        drawTexture(matrices, this.x, this.y, mode.u, mode.v + v, this.width, this.height, 512, 512);
        if (this.isHovered()) {
            renderTooltip(matrices, mouseX, mouseY);
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
