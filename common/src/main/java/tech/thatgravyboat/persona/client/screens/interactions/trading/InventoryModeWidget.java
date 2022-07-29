package tech.thatgravyboat.persona.client.screens.interactions.trading;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import tech.thatgravyboat.persona.Personas;

import java.util.function.Consumer;

public class InventoryModeWidget extends ButtonWidget {

    private static final Identifier BACKGROUND = new Identifier(Personas.MOD_ID, "textures/trading.png");

    public Mode mode = Mode.CREATIVE;
    private final Consumer<Mode> onPress;

    public InventoryModeWidget(int x, int y, int width, int height, Consumer<Mode> onPress) {
        super(x, y, width, height, new LiteralText(""), p -> {});
        this.onPress = onPress;
    }

    @Override
    public void onPress() {
        this.mode = this.mode.equals(Mode.INVENTORY) ? Mode.CREATIVE : Mode.INVENTORY;
        onPress.accept(this.mode);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int v = this.isHovered() ? 12 : 0;
        drawTexture(matrices, this.x, this.y, mode.u, mode.v + v, this.width, this.height);
    }

    public enum Mode {
        INVENTORY(138, 115),
        CREATIVE(150, 115);

        public final int u;
        public final int v;

        Mode(int u, int v) {
            this.u = u;
            this.v = v;
        }
    }
}
