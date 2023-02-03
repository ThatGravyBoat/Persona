package tech.thatgravyboat.persona.client.screens.interactions;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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

    private static final ResourceLocation BACKGROUND = new ResourceLocation(Personas.MOD_ID, "textures/interactions.png");

    private AddButtonWidget addButton = null;
    private ChangeListWidget typeButton = null;

    private final List<ChangeModeWidget> modes = new ArrayList<>();
    private final List<EditBox> textFields = new ArrayList<>();

    private final String id;
    private final String displayName;
    private final BlockPos pos;
    private final Appearance<?> appearance;
    private final Features features;

    public InteractionScreen(String id, String displayName, BlockPos pos, Appearance<?> appearance, Features features) {
        super(CommonComponents.EMPTY);
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
        addButton = addElement(new AddButtonWidget(x+232, y+4, 12,12, CommonComponents.EMPTY, p -> {
            ChangeModeWidget mode = addElement(new ChangeModeWidget(x + 9, y + 18 + (modes.size() * 11), 11, 11));
            EditBox text = addElement(new EditBox(this.font, x + 22, y + 19 + (modes.size() * 11), 220, 9, CommonComponents.EMPTY));
            modes.add(mode);
            textFields.add(text);
            if (modes.size() == 10) {
                addButton.active = false;
            }
        }));
        typeButton = addElement(new ChangeListWidget(x+218, y + 4, 12, 12,
                (button, matrices, mouseX, mouseY) ->
                        this.renderTooltip(matrices,
                                Component.literal("Mode: " + this.typeButton.getMode().name().toLowerCase(Locale.ROOT)),
                                mouseX, mouseY)));

        addElement(new Button(x + 5, y + 130, 75, 20, Component.literal("Trade menu"),
                p -> minecraft.setScreen(new TradeScreen(this.id, this.displayName, this.pos, this.appearance, this.features))));

        addElement(new NextPageButton(x + 227, y + 130, 20, 20, b -> {
            NetPacketHandler.sendToServer(new CreatePersonaMessage(new NpcData(id, this.displayName, this.appearance, createInteraction(), this.features), this.pos));
            this.onClose();
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
                interaction = new ChatInteraction(Component.literal(this.textFields.get(i).getValue()));
            } else {
                interaction = new CommandInteraction(this.textFields.get(i).getValue());
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
    public void resize(Minecraft client, int width, int height) {
        int oldHeight = this.height;
        int oldWidth = this.width;
        super.resize(client, width, height);
        for (ChangeModeWidget mode : this.modes) {
            mode.x = mode.x - (oldWidth / 2) + (width / 2);
            mode.y = mode.y - (oldHeight / 2) + (height / 2);
            this.addElement(mode);
        }
        for (EditBox field : this.textFields) {
            field.x = field.x - (oldWidth / 2) + (width / 2);
            field.y = field.y - (oldHeight / 2) + (height / 2);
            this.addElement(field);
        }
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        blit(matrices, this.width / 2 - 126, this.height / 2 - 77, 0,0, 252, 155, 512, 512);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public <T extends Widget & GuiEventListener & NarratableEntry> T addElement(T element) {
        return addWidget(addRenderableWidget(element));
    }
}
