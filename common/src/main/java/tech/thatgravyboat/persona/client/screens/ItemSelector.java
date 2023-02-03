package tech.thatgravyboat.persona.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.client.screens.interactions.trading.InventoryModeWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ItemSelector extends AbstractContainerEventHandler implements Widget, GuiEventListener, NarratableEntry {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(Personas.MOD_ID, "textures/trading.png");

    private final List<GuiEventListener> children = new ArrayList<>();
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
        viewableItems = Registry.ITEM.stream().map(Item::getDefaultInstance).filter(predicate).toList();
    }

    public void init() {
        var searchBar = addChild(new EditBox(this.parent.getText(), x+8, y+5, 80, 10, Component.literal("")));
        var modeButton = addChild(new InventoryModeWidget(x + 90, y + 4, 12, 12,
                p -> updateViewableItems(p, searchBar.getValue())));
        searchBar.setBordered(false);
        searchBar.setResponder(s -> updateViewableItems(modeButton.mode, s));
    }

    public void updateViewableItems(InventoryModeWidget.Mode mode, String input) {
        if (mode.equals(InventoryModeWidget.Mode.INVENTORY)) {
            if (Minecraft.getInstance().player != null) {
                viewableItems = Minecraft.getInstance().player.getInventory().items
                        .stream()
                        .filter(itemPredicate)
                        .filter(item -> !item.isEmpty())
                        .filter(item -> item.getHoverName().getString().toLowerCase(Locale.ROOT).contains(input.toLowerCase(Locale.ROOT)))
                        .toList();
            }
        } else {
            viewableItems = Registry.ITEM.stream()
                    .filter(item -> item.getName(item.getDefaultInstance()).getString().toLowerCase(Locale.ROOT).contains(input.toLowerCase(Locale.ROOT)))
                    .map(Item::getDefaultInstance)
                    .filter(itemPredicate)
                    .toList();
        }
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        blit(matrices, this.x, this.y, 138,0, 110, 115);

        for (GuiEventListener child : children()) {
            if (child instanceof Widget drawable) {
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
            blit(matrices, x, y, 162,v, 16, 18);
            var itemRenderer = Minecraft.getInstance().getItemRenderer();
            itemRenderer.renderGuiItem(viewableItems.get(i), x, y+1);
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
    public List<GuiEventListener> children() {
        return children;
    }

    public <T extends GuiEventListener> T addChild(T child) {
        children.add(child);
        return child;
    }



    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    public void setClickCallBack(Consumer<ItemStack> callBack) {
        this.clickCallback = callBack;
    }

    public boolean isListening() {
        return this.clickCallback != null;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {

    }
}
