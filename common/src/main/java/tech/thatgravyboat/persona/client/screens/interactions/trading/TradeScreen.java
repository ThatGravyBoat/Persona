package tech.thatgravyboat.persona.client.screens.interactions.trading;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.api.Features;
import tech.thatgravyboat.persona.api.NpcData;
import tech.thatgravyboat.persona.api.appearance.Appearance;
import tech.thatgravyboat.persona.api.interactions.Interaction;
import tech.thatgravyboat.persona.api.interactions.types.NothingInteraction;
import tech.thatgravyboat.persona.api.interactions.types.TradeInteraction;
import tech.thatgravyboat.persona.client.screens.ItemSelector;
import tech.thatgravyboat.persona.client.screens.ScreenHelper;
import tech.thatgravyboat.persona.client.screens.appearance.NextPageButton;
import tech.thatgravyboat.persona.client.screens.interactions.AddButtonWidget;
import tech.thatgravyboat.persona.common.network.NetPacketHandler;
import tech.thatgravyboat.persona.common.network.messages.server.CreatePersonaMessage;
import tech.thatgravyboat.persona.common.utils.ItemTradeListing;

import java.util.ArrayList;
import java.util.List;

public class TradeScreen extends Screen implements ScreenHelper {

    private static final Identifier BACKGROUND = new Identifier(Personas.MOD_ID, "textures/trading.png");

    private final String id;
    private final String displayName;
    private final BlockPos pos;
    private final Appearance<?> appearance;
    private final Features features;

    public TradeScreen(String id, String displayName, BlockPos pos, Appearance<?> appearance, Features features) {
        super(new LiteralText(""));
        this.id = id;
        this.displayName = displayName;
        this.pos = pos;
        this.appearance = appearance;
        this.features = features;
    }

    private AddButtonWidget addButton = null;
    private ItemSelector selector;
    private final List<TradeWidget> widgets = new ArrayList<>();
    private final List<ItemStack> itemsTooltips = new ArrayList<>();

    @Override
    protected void init() {
        super.init();
        int x = this.width / 2 - 69;
        int y = this.height / 2 - 69;

        this.selector = new ItemSelector(this, x + 150, this.height/2 - 57);
        this.addButton = addElement(new AddButtonWidget(x+117, y+4, 12, 12, new LiteralText(""), p -> {
            this.widgets.add(addElement(new TradeWidget(this, x+9, y + 18 + (this.widgets.size() * 18), this.selector)));
            if (widgets.size() == 5) {
                addButton.active = false;
            }
        }));

        addElement(new NextPageButton(x + 113, y + 113, 20, 20, b -> {
            NetPacketHandler.sendToServer(new CreatePersonaMessage(new NpcData(id, this.displayName, this.appearance, createInteraction(), this.features), this.pos));
            this.close();
        }));
    }

    private Interaction<?> createInteraction() {
        if (this.widgets.isEmpty()) {
            return new NothingInteraction();
        }
        List<ItemTradeListing> trades = new ArrayList<>();
        for (TradeWidget widget : this.widgets) {
            trades.add(new ItemTradeListing(widget.getPrimary(), widget.getSecondary(), widget.getOutput(),
                    1000, 1, 1f));
        }
        return new TradeInteraction(trades);
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        int x = width / 2 - 69;
        int y = height / 2 - 64;
        for (int i = 0; i < this.widgets.size(); i++) {
            var tradeWidget = this.widgets.get(i);
            tradeWidget.x = x + 9;
            tradeWidget.y = y + 18 + (i * 18);
            tradeWidget.setSelector(this.selector);
            tradeWidget.init();
            this.addElement(tradeWidget);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.selector.isListening()) {
            if (mouseX > this.selector.x && mouseX < this.selector.x + 111) {
                if (mouseY > this.selector.y && mouseY < this.selector.y + 116) {
                    this.selector.mouseClicked(mouseX, mouseY, button);
                    this.setFocused(this.selector);
                    return true;
                }
            }
            this.selector.setClickCallBack(null);
            this.remove(this.selector);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    //This is needed to make the method public
    @Override
    public void remove(Element child) {
        super.remove(child);
    }

    public void renderTooltip(ItemStack stack) {
        itemsTooltips.add(stack);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        itemsTooltips.clear();
        renderBackground(matrices);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexture(matrices, this.width / 2 - 69, this.height / 2 - 69, 0,0, 138, 138);
        super.render(matrices, mouseX, mouseY, delta);

        if (this.selector.isListening()) this.selector.render(matrices, mouseX, mouseY, delta);
        for (ItemStack itemsTooltip : itemsTooltips) {
            renderTooltip(matrices, itemsTooltip, mouseX, mouseY);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public <T extends Drawable & Selectable & Element> T addElement(T element) {
        return addDrawable(addSelectableChild(element));
    }

    public TextRenderer getText() {
        return this.textRenderer;
    }
}
