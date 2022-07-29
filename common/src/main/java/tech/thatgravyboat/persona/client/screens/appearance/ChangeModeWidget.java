package tech.thatgravyboat.persona.client.screens.appearance;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import tech.thatgravyboat.persona.Personas;

import java.util.function.Consumer;

public class ChangeModeWidget extends ButtonWidget {

    private static final Identifier BACKGROUND = new Identifier(Personas.MOD_ID, "textures/appearance.png");

    private Mode mode;
    private final Consumer<Mode> onPress;

    public ChangeModeWidget(Mode mode, int x, int y, int width, int height, Consumer<Mode> onPress, TooltipSupplier tooltipSupplier) {
        super(x, y, width, height, new LiteralText(""), p -> {}, tooltipSupplier);
        this.mode = mode;
        this.onPress = onPress;
    }

    @Override
    public void onPress() {
        this.mode = this.mode.next();
        onPress.accept(this.mode);
    }

    @Override
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int v = this.isHovered() ? 20 : 0;
        drawTexture(matrices, this.x, this.y, mode.u, mode.v + v, this.width, this.height);
        if (this.isHovered()) {
            renderTooltip(matrices, mouseX, mouseY);
        }
    }

    public enum Mode {
        NPC(0, 128),
        GECKOLIB(40, 128),
        ENTITY(20, 128);

        public final int u;
        public final int v;

        Mode(int u, int v) {
            this.u = u;
            this.v = v;
        }

        public Mode next() {
            return switch (this) {
                case NPC -> GECKOLIB;
                case GECKOLIB -> ENTITY;
                case ENTITY -> NPC;
            };
        }
    }
}
