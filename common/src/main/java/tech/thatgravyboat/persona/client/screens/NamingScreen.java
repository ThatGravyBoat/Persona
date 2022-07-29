package tech.thatgravyboat.persona.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.client.screens.appearance.AppearanceScreen;
import tech.thatgravyboat.persona.client.screens.interactions.trading.TradeScreen;
import tech.thatgravyboat.persona.common.lib.State;
import tech.thatgravyboat.persona.common.network.NetPacketHandler;
import tech.thatgravyboat.persona.common.network.messages.server.CheckIdMessage;
import tech.thatgravyboat.persona.common.network.messages.server.SummonPersonaMessage;

import java.util.List;
import java.util.regex.Pattern;

public class NamingScreen extends Screen {

    private static final Identifier BACKGROUND = new Identifier(Personas.MOD_ID, "textures/namepicker.png");

    private static final Pattern VALID = Pattern.compile("\\w+");

    private TextFieldWidget idInput;
    private ButtonWidget commitButton;

    private State state = State.VALID;

    private final BlockPos pos;

    public NamingScreen(BlockPos pos) {
        super(new LiteralText("title"));
        this.pos = pos;
    }

    @Override
    protected void init() {
        idInput = new TextFieldWidget(this.textRenderer, (this.width / 2) - 67, (this.height / 2) - 30, 132, 20, new LiteralText(""));
        idInput.setChangedListener(input -> {
            if (input.length() > 2) {
                setState(VALID.matcher(input).matches() ? State.VALID : State.INVALID);
                commitButton.active = true;
            } else {
                commitButton.active = false;
            }
        });
        idInput.setMaxLength(50);
        addSelectableChild(idInput);
        commitButton = new ButtonWidget((this.width / 2) - 67, (this.height / 2) - 5, 132, 20, new LiteralText("Create"), (p) -> {
            if (state.equals(State.VALID)) {
                NetPacketHandler.sendToServer(new CheckIdMessage(idInput.getText()));
            }
            if (state.equals(State.SERVER_VALID)) {
                MinecraftClient.getInstance().setScreen(new AppearanceScreen(this.idInput.getText(), this.pos));
            }
            if (state.equals(State.ALREADY_EXISTS)) {
                NetPacketHandler.sendToServer(new SummonPersonaMessage(idInput.getText(), this.pos));
                this.close();
            }
        });
        commitButton.active = false;
        addSelectableChild(commitButton);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexture(matrices, this.width / 2 - 88, this.height / 2 - 45, 0,0, 176, 90);

        idInput.render(matrices, mouseX, mouseY, delta);
        commitButton.render(matrices, mouseX, mouseY, delta);

        List<OrderedText> orderedTexts = this.client.textRenderer.wrapLines(this.state.displayText, 170);
        for (int i = 0; i < Math.min(orderedTexts.size(), 2); i++) {
            textRenderer.drawWithShadow(matrices, orderedTexts.get(i), (float)((this.width / 2) - textRenderer.getWidth(orderedTexts.get(i)) / 2), (this.height / 2f) + 20f + (9*i), this.state.color);
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public void setStateForId(State state, String id) {
        if (this.idInput.getText().equals(id)) {
            setState(state);
        }
    }

    public void setState(State state) {
        this.state = state;
        this.commitButton.active = !this.state.equals(State.INVALID);
        switch (this.state) {
            case ALREADY_EXISTS ->  this.commitButton.setMessage(new LiteralText("Summon"));
            case SERVER_VALID ->  this.commitButton.setMessage(new LiteralText("Confirm"));
            default -> this.commitButton.setMessage(new LiteralText("Create"));
        }
    }

}
