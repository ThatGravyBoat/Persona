package tech.thatgravyboat.persona.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.common.entity.Persona;
import tech.thatgravyboat.persona.common.network.NetPacketHandler;
import tech.thatgravyboat.persona.common.network.messages.server.PersonaFaceMessage;
import tech.thatgravyboat.persona.common.network.messages.server.RemoveEntityMessage;
import tech.thatgravyboat.persona.common.network.messages.server.SelectEntityMessage;

public class DebugScreen extends Screen {

    private static final Identifier BACKGROUND = new Identifier(Personas.MOD_ID, "textures/namepicker.png");

    private final Persona persona;

    public DebugScreen(Persona persona) {
        super(new LiteralText(""));
        this.persona = persona;
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 88;
        int y = this.height / 2 - 45;
        addElement(new ButtonWidget(x+38, y + 20, 100, 20, new LiteralText("Remove"), p -> {
            NetPacketHandler.sendToServer(new RemoveEntityMessage(persona.getId()));
            this.close();
        }));
        addElement(new ButtonWidget(x+38, y+40, 100, 20, new LiteralText("Face Me"), p -> {
            NetPacketHandler.sendToServer(new PersonaFaceMessage(persona.getId()));
            this.close();
        })).active = persona.getFeatures() == null || !persona.getFeatures().shouldFacePlayer();
        addElement(new ButtonWidget(x+38, y+60, 100, 20, new LiteralText("Teleport"), p -> {
            NetPacketHandler.sendToServer(new SelectEntityMessage(persona.getId()));
            this.close();
        }));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexture(matrices, this.width / 2 - 88, this.height / 2 - 45, 0,0, 176, 90);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public <T extends Drawable & Selectable & Element> T addElement(T element) {
        return addDrawable(addSelectableChild(element));
    }
}
