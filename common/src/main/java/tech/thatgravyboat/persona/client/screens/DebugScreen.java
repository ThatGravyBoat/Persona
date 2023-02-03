package tech.thatgravyboat.persona.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.common.entity.Persona;
import tech.thatgravyboat.persona.common.network.NetPacketHandler;
import tech.thatgravyboat.persona.common.network.messages.server.PersonaFaceMessage;
import tech.thatgravyboat.persona.common.network.messages.server.RemoveEntityMessage;
import tech.thatgravyboat.persona.common.network.messages.server.SelectEntityMessage;

public class DebugScreen extends Screen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(Personas.MOD_ID, "textures/namepicker.png");

    private final Persona persona;

    public DebugScreen(Persona persona) {
        super(Component.literal(""));
        this.persona = persona;
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 88;
        int y = this.height / 2 - 45;
        addElement(new Button(x+38, y + 20, 100, 20, Component.literal("Remove"), p -> {
            NetPacketHandler.sendToServer(new RemoveEntityMessage(persona.getId()));
            this.onClose();
        }));
        addElement(new Button(x+38, y+40, 100, 20, Component.literal("Face Me"), p -> {
            NetPacketHandler.sendToServer(new PersonaFaceMessage(persona.getId()));
            this.onClose();
        })).active = persona.getFeatures() == null || !persona.getFeatures().shouldFacePlayer();
        addElement(new Button(x+38, y+60, 100, 20, Component.literal("Teleport"), p -> {
            NetPacketHandler.sendToServer(new SelectEntityMessage(persona.getId()));
            this.onClose();
        }));
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        blit(matrices, this.width / 2 - 88, this.height / 2 - 45, 0,0, 176, 90);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public <T extends Widget & NarratableEntry & GuiEventListener> T addElement(T element) {
        return addWidget(addRenderableWidget(element));
    }
}
