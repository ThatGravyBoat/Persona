package tech.thatgravyboat.persona.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.client.screens.appearance.AppearanceScreen;
import tech.thatgravyboat.persona.common.lib.State;
import tech.thatgravyboat.persona.common.network.NetPacketHandler;
import tech.thatgravyboat.persona.common.network.messages.server.CheckIdMessage;
import tech.thatgravyboat.persona.common.network.messages.server.SummonPersonaMessage;

import java.util.List;
import java.util.regex.Pattern;

public class NamingScreen extends Screen {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(Personas.MOD_ID, "textures/namepicker.png");

    private static final Pattern VALID = Pattern.compile("\\w+");

    private EditBox idInput;
    private Button commitButton;

    private State state = State.VALID;

    private final BlockPos pos;

    public NamingScreen(BlockPos pos) {
        super(Component.literal("title"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        idInput = new EditBox( this.font, (this.width / 2) - 67, (this.height / 2) - 30, 132, 20, Component.literal(""));
        idInput.setResponder(input -> {
            if (input.length() > 2) {
                setState(VALID.matcher(input).matches() ? State.VALID : State.INVALID);
                commitButton.active = true;
            } else {
                commitButton.active = false;
            }
        });
        idInput.setMaxLength(50);
        addWidget(idInput);
        commitButton = new Button((this.width / 2) - 67, (this.height / 2) - 5, 132, 20, Component.literal("Create"), (p) -> {
            if (state.equals(State.VALID)) {
                NetPacketHandler.sendToServer(new CheckIdMessage(idInput.getValue()));
            }
            if (state.equals(State.SERVER_VALID)) {
                Minecraft.getInstance().setScreen(new AppearanceScreen(this.idInput.getValue(), this.pos));
            }
            if (state.equals(State.ALREADY_EXISTS)) {
                NetPacketHandler.sendToServer(new SummonPersonaMessage(idInput.getValue(), this.pos));
                this.onClose();
            }
        });
        commitButton.active = false;
        addWidget(commitButton);
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        blit(matrices, this.width / 2 - 88, this.height / 2 - 45, 0,0, 176, 90);

        idInput.render(matrices, mouseX, mouseY, delta);
        commitButton.render(matrices, mouseX, mouseY, delta);

        List<FormattedCharSequence> orderedTexts = this.minecraft.font.split(this.state.displayText, 170);
        for (int i = 0; i < Math.min(orderedTexts.size(), 2); i++) {
            font.draw(matrices, orderedTexts.get(i), (float)((this.width / 2) - font.width(orderedTexts.get(i)) / 2), (this.height / 2f) + 20f + (9*i), this.state.color);
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public void setStateForId(State state, String id) {
        if (this.idInput.getValue().equals(id)) {
            setState(state);
        }
    }

    public void setState(State state) {
        this.state = state;
        this.commitButton.active = !this.state.equals(State.INVALID);
        switch (this.state) {
            case ALREADY_EXISTS ->  this.commitButton.setMessage(Component.literal("Summon"));
            case SERVER_VALID ->  this.commitButton.setMessage(Component.literal("Confirm"));
            default -> this.commitButton.setMessage(Component.literal("Create"));
        }
    }

}
