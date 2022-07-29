package tech.thatgravyboat.persona.client.screens.interactions;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.api.Features;
import tech.thatgravyboat.persona.api.NpcData;
import tech.thatgravyboat.persona.api.appearance.Appearance;
import tech.thatgravyboat.persona.api.interactions.Interaction;
import tech.thatgravyboat.persona.api.interactions.types.*;
import tech.thatgravyboat.persona.client.screens.appearance.NextPageButton;
import tech.thatgravyboat.persona.client.screens.interactions.trading.TradeScreen;
import tech.thatgravyboat.persona.common.network.NetPacketHandler;
import tech.thatgravyboat.persona.common.network.messages.server.CreatePersonaMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InteractionScreen extends Screen {

    private static final Identifier BACKGROUND = new Identifier(Personas.MOD_ID, "textures/interactions.png");

    private AddButtonWidget addButton = null;
    private ChangeListWidget typeButton = null;

    private final List<ChangeModeWidget> modes = new ArrayList<>();
    private final List<TextFieldWidget> textFields = new ArrayList<>();

    private final String id;
    private final String displayName;
    private final BlockPos pos;
    private final Appearance<?> appearance;
    private final Features features;

    public InteractionScreen(String id, String displayName, BlockPos pos, Appearance<?> appearance, Features features) {
        super(new LiteralText(""));
        this.id = id;
        this.displayName = displayName;
        this.pos = pos;
        this.appearance = appearance;
        this.features = features;
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 126;
        int y = this.height / 2 - 77;
        addButton = addElement(new AddButtonWidget(x+232, y+4, 12,12, new LiteralText(""), p -> {
            ChangeModeWidget mode = addElement(new ChangeModeWidget(x + 9, y + 18 + (modes.size() * 11), 11, 11));
            TextFieldWidget text = addElement(new TextFieldWidget(this.textRenderer, x + 22, y + 19 + (modes.size() * 11), 220, 9, new LiteralText("")));
            modes.add(mode);
            textFields.add(text);
            if (modes.size() == 10) {
                addButton.active = false;
            }
        }));
        typeButton = addElement(new ChangeListWidget(x+218, y + 4, 12, 12,
                (button, matrices, mouseX, mouseY) ->
                        this.renderTooltip(matrices,
                                new LiteralText("Mode: " + this.typeButton.getMode().name().toLowerCase(Locale.ROOT)),
                                mouseX, mouseY)));

        addElement(new ButtonWidget(x + 5, y + 130, 75, 20, new LiteralText("Trade menu"),
                p -> client.setScreen(new TradeScreen(this.id, this.displayName, this.pos, this.appearance, this.features))));

        addElement(new NextPageButton(x + 227, y + 130, 20, 20, b -> {
            NetPacketHandler.sendToServer(new CreatePersonaMessage(new NpcData(id, this.displayName, this.appearance, createInteraction(), this.features), this.pos));
            this.close();
        }));
    }

    private Interaction<?> createInteraction() {
        if (this.modes.isEmpty()) {
            return new NothingInteraction();
        }
        List<Interaction<?>> interactions = new ArrayList<>();
        for (int i = 0; i < this.modes.size(); i++) {
            ChangeModeWidget mode = this.modes.get(i);
            Interaction<?> interaction;
            if (mode.getMode().equals(ChangeModeWidget.Mode.CHAT)) {
                interaction = new ChatInteraction(new LiteralText(this.textFields.get(i).getText()));
            } else {
                interaction = new CommandInteraction(this.textFields.get(i).getText());
            }
            if (this.typeButton.getMode().equals(ChangeListWidget.Mode.DELAYED) && i > 0) {
                interaction = new DelayedInteraction(i * 500, interaction);
            }
            interactions.add(interaction);
        }
        if (interactions.size() == 1) return interactions.get(0);
        return this.typeButton.getMode().equals(ChangeListWidget.Mode.RANDOM) ? new RandomInteraction(interactions) : new ListInteraction(interactions);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        int oldHeight = this.height;
        int oldWidth = this.width;
        super.resize(client, width, height);
        for (ChangeModeWidget mode : this.modes) {
            mode.x = mode.x - (oldWidth / 2) + (width / 2);
            mode.y = mode.y - (oldHeight / 2) + (height / 2);
            this.addElement(mode);
        }
        for (TextFieldWidget field : this.textFields) {
            field.x = field.x - (oldWidth / 2) + (width / 2);
            field.y = field.y - (oldHeight / 2) + (height / 2);
            this.addElement(field);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexture(matrices, this.width / 2 - 126, this.height / 2 - 77, 0,0, 252, 155, 512, 512);
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
