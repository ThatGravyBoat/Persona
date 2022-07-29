package tech.thatgravyboat.persona.client.screens.interactions.trading;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.client.screens.ItemSelector;

public class TradeWidget extends ClickableWidget {

    private static final Identifier BACKGROUND = new Identifier(Personas.MOD_ID, "textures/trading.png");

    private final TradeScreen screen;
    private ItemSelector selector;

    private ItemStack primary = ItemStack.EMPTY;
    private IncrementButton primaryCounter;
    private ItemStack secondary = ItemStack.EMPTY;
    private IncrementButton secondaryCounter;
    private ItemStack output = ItemStack.EMPTY;
    private IncrementButton outputCounter;

    public TradeWidget(TradeScreen screen, int x, int y, ItemSelector selector) {
        super(x, y, 120, 18, new LiteralText(""));
        this.screen = screen;
        this.selector = selector;
        init();
    }

    public void init() {
        this.primaryCounter = new IncrementButton(this.x + 17, this.y + 1);
        this.secondaryCounter = new IncrementButton(this.x + 52, this.y + 1);
        this.outputCounter = new IncrementButton(this.x + 101, this.y + 1);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        this.primaryCounter.onClick(mouseX, mouseY);
        this.secondaryCounter.onClick(mouseX, mouseY);
        this.outputCounter.onClick(mouseX, mouseY);
        mouseX-=this.x;
        mouseY-=this.y;

        boolean addSelector = false;
        if (mouseX > 1 && mouseX < 18 && mouseY > 1 && mouseY < 18) {
            addSelector = true;
            this.selector.setClickCallBack(item -> {
                this.primary = item.copy();
                this.selector.setClickCallBack(null);
                this.screen.remove(this.selector);
                updateCounter(this.primaryCounter, this.primary);
            });
        }

        if (mouseX > 36 && mouseX < 53 && mouseY > 1 && mouseY < 18) {
            addSelector = true;
            this.selector.setClickCallBack(item -> {
                this.secondary = item.copy();
                this.selector.setClickCallBack(null);
                this.screen.remove(this.selector);
                updateCounter(this.secondaryCounter, this.secondary);
            });
        }

        if (mouseX > 85 && mouseX < 102 && mouseY > 1 && mouseY < 18) {
            addSelector = true;
            this.selector.setClickCallBack(item -> {
                this.output = item.copy();
                this.selector.setClickCallBack(null);
                this.screen.remove(this.selector);
                updateCounter(this.outputCounter, this.output);
            });
        }

        if (addSelector && !this.screen.children().contains(this.selector)) {
            this.screen.addElement(this.selector);
        }
    }

    private void updateCounter(IncrementButton counter, ItemStack item) {
        counter.setBounds(1, item.isOf(Items.AIR) ? 1 : item.getMaxCount());
        counter.setCallback(() -> item);
        counter.setCount(1);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexture(matrices, this.x, this.y, 0,138, 120, 18);

        var itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        itemRenderer.renderGuiItemIcon(primary, this.x + 1, this.y + 1);
        itemRenderer.renderGuiItemOverlay(this.screen.getText(), primary, this.x + 1, this.y + 1);
        itemRenderer.renderGuiItemIcon(secondary, this.x + 36, this.y + 1);
        itemRenderer.renderGuiItemOverlay(this.screen.getText(), secondary, this.x + 36, this.y + 1);
        itemRenderer.renderGuiItemIcon(output, this.x + 85, this.y + 1);
        itemRenderer.renderGuiItemOverlay(this.screen.getText(), output, this.x + 85, this.y + 1);

        if (this.primaryCounter != null) this.primaryCounter.render(matrices, mouseX, mouseY, delta);
        if (this.secondaryCounter != null) this.secondaryCounter.render(matrices, mouseX, mouseY, delta);
        if (this.outputCounter != null) this.outputCounter.render(matrices, mouseX, mouseY, delta);

        int tempX = mouseX - this.x;
        int tempY = mouseY - this.y;

        if (tempX > 1 && tempX < 18 && tempY > 1 && tempY < 18 && !this.primary.isEmpty()) {
            this.screen.renderTooltip(this.primary);
        }

        if (tempX > 36 && tempX < 53 && tempY > 1 && tempY < 18 && !this.secondary.isEmpty()) {
            this.screen.renderTooltip(this.secondary);
        }

        if (tempX > 85 && tempX < 102 && tempY > 1 && tempY < 18 && !this.output.isEmpty()) {
            this.screen.renderTooltip(this.output);
        }
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {}

    public void setSelector(ItemSelector selector) {
        this.selector = selector;
    }

    public ItemStack getPrimary() {
        return primary;
    }

    public ItemStack getSecondary() {
        return secondary;
    }

    public ItemStack getOutput() {
        return output;
    }
}
