package tech.thatgravyboat.persona.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.client.screens.interactions.trading.InventoryModeWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ItemSelector extends AbstractParentElement implements Drawable, Element, Selectable {

    private static final Identifier BACKGROUND = new Identifier(Personas.MOD_ID, "textures/trading.png");

    private final List<Element> children = new ArrayList<>();
    private List<ItemStack> viewableItems;

    private final ScreenHelper parent;
    public int x;
    public int y;

    private Consumer<ItemStack> clickCallback = null;
    private final Predicate<ItemStack> itemPredicate;

    public ItemSelector(ScreenHelper parent, int x, int y) {
        this(parent, x, y, i -> true);
    }

    public ItemSelector(ScreenHelper parent, int x, int y, Predicate<ItemStack> predicate) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        init();
        this.itemPredicate = predicate;
        viewableItems = Registry.ITEM.stream().map(Item::getDefaultStack).filter(predicate).toList();
    }

    public void init() {
        var searchBar = addChild(new TextFieldWidget(this.parent.getText(), x+8, y+5, 80, 10, new LiteralText("")));
        var modeButton = addChild(new InventoryModeWidget(x + 90, y + 4, 12, 12,
                p -> updateViewableItems(p, searchBar.getText())));
        searchBar.setDrawsBackground(false);
        searchBar.setChangedListener(s -> updateViewableItems(modeButton.mode, s));
    }

    public void updateViewableItems(InventoryModeWidget.Mode mode, String input) {
        if (mode.equals(InventoryModeWidget.Mode.INVENTORY)) {
            if (MinecraftClient.getInstance().player != null) {
                viewableItems = MinecraftClient.getInstance().player.getInventory().main
                        .stream()
                        .filter(itemPredicate)
                        .filter(item -> !item.isEmpty())
                        .filter(item -> item.getName().getString().toLowerCase(Locale.ROOT).contains(input.toLowerCase(Locale.ROOT)))
                        .toList();
            }
        } else {
            viewableItems = Registry.ITEM.stream()
                    .filter(item -> item.getName().getString().toLowerCase(Locale.ROOT).contains(input.toLowerCase(Locale.ROOT)))
                    .map(Item::getDefaultStack)
                    .filter(itemPredicate)
                    .toList();
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexture(matrices, this.x, this.y, 138,0, 110, 115);

        for (Element child : children()) {
            if (child instanceof Drawable drawable) {
                drawable.render(matrices, mouseX, mouseY, delta);
            }
        }

        ItemStack hoveredStack = null;

        for (int i = 0; i < Math.min(viewableItems.size(), 30); i++) {
            int y = i / 6;
            y = this.y + 18 + (y * 18);
            int x = i % 6;
            x = this.x + 7 + (x * 16);
            boolean hovered = mouseX > x && mouseX < x + 17 && mouseY > y && mouseY < y + 19;
            int v = hovered ? 133 : 115;
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, BACKGROUND);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            drawTexture(matrices, x, y, 162,v, 16, 18);
            var itemRenderer = MinecraftClient.getInstance().getItemRenderer();
            itemRenderer.renderGuiItemIcon(viewableItems.get(i), x, y+1);
            if (hovered) {
                hoveredStack = viewableItems.get(i);
            }
        }

        if (hoveredStack != null) {
            parent.renderTooltip(hoveredStack);
        }

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (int i = 0; i < Math.min(viewableItems.size(), 30); i++) {
            int y = i / 6;
            y = this.y + 18 + (y * 18);
            int x = i % 6;
            x = this.x + 7 + (x * 16);
            boolean hovered = mouseX > x && mouseX < x + 17 && mouseY > y && mouseY < y + 19;
            if (hovered) {
                if (clickCallback != null) {
                    clickCallback.accept(viewableItems.get(i));
                }
                return true;
            }
        }
        super.mouseClicked(mouseX, mouseY, button);
        return true;
    }

    @Override
    public List<Element> children() {
        return children;
    }

    public <T extends Element> T addChild(T child) {
        children.add(child);
        return child;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    public void setClickCallBack(Consumer<ItemStack> callBack) {
        this.clickCallback = callBack;
    }

    public boolean isListening() {
        return this.clickCallback != null;
    }
}
