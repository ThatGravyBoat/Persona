package tech.thatgravyboat.persona.client.screens.appearance;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
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
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.api.Features;
import tech.thatgravyboat.persona.api.appearance.Appearance;
import tech.thatgravyboat.persona.api.appearance.appearances.EntityAppearance;
import tech.thatgravyboat.persona.api.appearance.appearances.GeckoLibAppearance;
import tech.thatgravyboat.persona.api.appearance.appearances.NpcAppearance;
import tech.thatgravyboat.persona.client.screens.ItemSelector;
import tech.thatgravyboat.persona.client.screens.LabelTextField;
import tech.thatgravyboat.persona.client.screens.ScreenHelper;
import tech.thatgravyboat.persona.client.screens.interactions.InteractionScreen;
import tech.thatgravyboat.persona.common.entity.Persona;
import tech.thatgravyboat.persona.common.registry.Registry;
import tech.thatgravyboat.persona.common.utils.ClientRenderUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

public class AppearanceScreen extends Screen implements ScreenHelper {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(Personas.MOD_ID, "textures/appearance.png");

    private final List<ItemStack> itemsTooltips = new ArrayList<>();

    private ChangeModeWidget.Mode mode = ChangeModeWidget.Mode.NPC;

    private static final Pattern VALID = Pattern.compile("[_A-Za-z0-9]+");

    private final String id;
    private final BlockPos pos;

    private String displayName = "";

    private boolean baby = false;
    private boolean sitting = false;
    private boolean nametag = false;
    private boolean facePlayer = false;

    private final Persona persona;

    private Button nextPage;

    public AppearanceScreen(String id, BlockPos pos) {
        super(CommonComponents.EMPTY);
        this.persona = new Persona(Registry.PERSONA.get(), Minecraft.getInstance().level, false);
        persona.setCustomName(Component.literal(this.displayName));
        this.id = id;
        this.pos = pos;
    }

    public void updateScreen() {
        clearWidgets();
        init();
    }

    @Override
    protected void init() {
        int x = this.width / 2 - 116;
        int y = this.height / 2 - 64;

        this.baby = false;
        this.sitting = false;
        this.nametag = false;

        this.nextPage = addElement(new NextPageButton(x + 204, y + 100, 20, 20,
                b -> minecraft.setScreen(new InteractionScreen(this.id, this.displayName, this.pos, createAppearance(), createFeatures()))));

        addElement(new ChangeModeWidget(mode,x + 204, y + 8, 20, 20, m -> {
            this.mode = m;
            updateScreen();
        }, (button, matrices, mouseX, mouseY) -> this.renderTooltip(matrices, "Mode: " + this.mode.name().toLowerCase(Locale.ROOT), mouseX, mouseY)));

        EditBox text = addElement(new EditBox(this.font, x + 9, y + 9, 73, 10, CommonComponents.EMPTY));
        text.setBordered(false);
        text.setResponder(s -> {
            this.displayName = s;
            persona.setCustomName(Component.literal(this.displayName));
        });

        addElement(new ChangeModeWidget(mode,x + 204, y + 8, 20, 20, m -> {
            this.mode = m;
            updateScreen();
        }, (button, matrices, mouseX, mouseY) -> this.renderTooltip(matrices, "Mode: " + this.mode.name().toLowerCase(Locale.ROOT), mouseX, mouseY)));

        switch (this.mode) {
            case NPC -> {
                addElement(new ToggleSwitchWidget(x + 204, y + 30, 60, 128, this::setBaby,
                        (stack, mouseX, mouseY, toggled) -> this.renderTooltip(stack, "Baby: " + toggled, mouseX, mouseY)));
                addElement(new ToggleSwitchWidget(x + 204, y + 42, 60, 148, this::setNameTag,
                        (stack, mouseX, mouseY, toggled) -> this.renderTooltip(stack, "Name Tag: " + toggled, mouseX, mouseY)));
                addElement(new ToggleSwitchWidget(x + 204, y + 54, 100, 128, this::setSitting,
                        (stack, mouseX, mouseY, toggled) -> this.renderTooltip(stack, "Sitting: " + toggled, mouseX, mouseY)));
                addElement(new ToggleSwitchWidget(x + 204, y + 66, 100, 148, this::setFacePlayer,
                        (stack, mouseX, mouseY, toggled) -> this.renderTooltip(stack, "Face Player: " + toggled, mouseX, mouseY)));
                var labelTextField = addElement(new LabelTextField(this.font, x + 88, y + 20, 110, 10, Component.literal("Username:")));
                labelTextField.setMaxLength(16);
                Button search = addElement(new Button(x + 88, y + 32, 110, 20, Component.literal("Search"), p -> {
                    var skin = SkinHelper.getSkin(labelTextField.getValue());
                    if (skin != null) {
                        persona.setAppearance(new NpcAppearance(skin.getFirst(), skin.getSecond()));
                        updateFeatures(EntityDimensions.scalable(0.6F, 1.8F));
                        checkNextButton();
                    }
                }));
                search.active = false;
                labelTextField.setResponder(s -> {
                    boolean valid = VALID.matcher(s).matches() && s.length() < 17 && s.length() > 2;
                    labelTextField.setTextColor(valid ? 0xffffff : 0xff0000);
                    search.active = valid;
                });
            }
            case ENTITY -> {
                addElement(new ToggleSwitchWidget(x + 204, y + 30, 60, 128, this::setBaby,
                        (stack, mouseX, mouseY, toggled) -> this.renderTooltip(stack, "Baby: " + toggled, mouseX, mouseY)));
                addElement(new ToggleSwitchWidget(x + 204, y + 42, 60, 148, this::setNameTag,
                        (stack, mouseX, mouseY, toggled) -> this.renderTooltip(stack, "Name Tag: " + toggled, mouseX, mouseY)));
                addElement(new ToggleSwitchWidget(x + 204, y + 54, 100, 148, this::setFacePlayer,
                        (stack, mouseX, mouseY, toggled) -> this.renderTooltip(stack, "Face Player: " + toggled, mouseX, mouseY)));
                var itemSelector = addElement(new ItemSelector(this, x + 88, y + 8, item -> item.getItem() instanceof SpawnEggItem));
                itemSelector.setClickCallBack(item -> {
                    if (item.getItem() instanceof SpawnEggItem spawnEggItem) {
                        EntityType<?> entityType = spawnEggItem.getType(null);
                        persona.setAppearance(new EntityAppearance(entityType));
                        updateFeatures(entityType.getDimensions());
                        checkNextButton();
                    }
                });
            }
            case GECKOLIB -> {
                addElement(new ToggleSwitchWidget(x + 204, y + 30, 60, 148, this::setNameTag,
                        (stack, mouseX, mouseY, toggled) -> this.renderTooltip(stack, "Name Tag: " + toggled, mouseX, mouseY)));
                addElement(new ToggleSwitchWidget(x + 204, y + 42, 100, 148, this::setFacePlayer,
                        (stack, mouseX, mouseY, toggled) -> this.renderTooltip(stack, "Face Player: " + toggled, mouseX, mouseY)));
                var modelText = addElement(new LabelTextField(this.font, x + 88, y + 20, 110, 10, Component.literal("Model:")));
                var textureText = addElement(new LabelTextField(this.font, x + 88, y + 42, 110, 10, Component.literal("Texture:")));
                var animation = addElement(new LabelTextField(this.font, x + 88, y + 64, 110, 10, Component.literal("Animation (Optional):")));

                modelText.setMaxLength(1000);
                textureText.setMaxLength(1000);
                animation.setMaxLength(1000);

                Button preview = addElement(new Button(x + 88, y + 76, 110, 20, Component.literal("Preview"), p -> {
                    if (isGeckoGood(modelText, textureText, animation)) {
                        persona.setAppearance(new GeckoLibAppearance(getId(modelText), getId(textureText), getId(animation)));
                        updateFeatures(EntityDimensions.scalable(0.6F, 1.8F));
                        checkNextButton();
                    }
                }));
                preview.active = false;

                modelText.setResponder(s -> {
                    ResourceLocation identifier = ResourceLocation.tryParse(s);
                    boolean isGood = identifier != null && identifier.getPath().endsWith(".geo.json") && identifier.getPath().startsWith("geo/");
                    modelText.setTextColor(isGood ? 0xffffff : 0xff0000);
                    preview.active = isGeckoGood(modelText, textureText, animation);
                });
                textureText.setResponder(s -> {
                    ResourceLocation identifier = ResourceLocation.tryParse(s);
                    boolean isGood = identifier != null && identifier.getPath().endsWith(".png") && identifier.getPath().startsWith("textures/entity/");
                    textureText.setTextColor(isGood ? 0xffffff : 0xff0000);
                    preview.active = isGeckoGood(modelText, textureText, animation);
                });
                animation.setResponder(s -> {
                    if (!Objects.equals(s, "")) {
                        ResourceLocation identifier = ResourceLocation.tryParse(s);
                        boolean isGood = identifier != null && identifier.getPath().endsWith(".animation.json") && identifier.getPath().startsWith("animations/");
                        animation.setTextColor(isGood ? 0xffffff : 0xff0000);
                    }
                    preview.active = isGeckoGood(modelText, textureText, animation);
                });
            }
        }

        this.persona.setAppearance(null);
        this.persona.setFeatures(null);
        checkNextButton();
    }

    private void checkNextButton() {
        this.nextPage.active = this.persona.getAppearance() != null && this.persona.getFeatures() != null;
    }

    private ResourceLocation getId(EditBox field) {
        return ResourceLocation.tryParse(field.getValue());
    }

    private boolean isGeckoGood(EditBox model, EditBox texture, EditBox animation) {
        ResourceLocation identifier = ResourceLocation.tryParse(model.getValue());
        boolean modelGood = identifier != null && identifier.getPath().endsWith(".geo.json") && identifier.getPath().startsWith("geo/");
        identifier = ResourceLocation.tryParse(texture.getValue());
        boolean textureGood = identifier != null && identifier.getPath().endsWith(".png") && identifier.getPath().startsWith("textures/entity/");
        identifier = ResourceLocation.tryParse(animation.getValue());
        boolean animationGood = animation.getValue().equals("") || (identifier != null && identifier.getPath().endsWith(".animation.json") && identifier.getPath().startsWith("animations/"));
        return modelGood && textureGood && animationGood;
    }

    private void setBaby(boolean baby) {
        this.baby = baby;
        updateFeatures(null);
    }

    private void setSitting(boolean sitting) {
        this.sitting = sitting;
        updateFeatures(null);
    }

    private void setNameTag(boolean nameTag) {
        this.nametag = nameTag;
        updateFeatures(null);
    }

    private void setFacePlayer(boolean facePlayer) {
        this.facePlayer = facePlayer;
    }

    private void updateFeatures(EntityDimensions dimensions) {
        EntityDimensions entityDimensions = dimensions != null ? dimensions :
                persona.getFeatures() != null ? persona.getFeatures().dimensions() : EntityDimensions.scalable(1, 2);
        persona.setFeatures(new Features(false, this.baby, this.sitting, entityDimensions, this.nametag));
    }

    private void renderTooltip(PoseStack stack, String text, int x, int y) {
        this.renderTooltip(stack, Component.literal(text), x, y);
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
        itemsTooltips.clear();
        renderBackground(matrices);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = this.width / 2 - 116;
        int y = this.height / 2 - 64;

        blit(matrices, x, this.height / 2 - 64, 0,0, 232, 128);
        super.render(matrices, mouseX, mouseY, delta);

        matrices.pushPose();

        float divider = Math.max(2f, persona.getFeatures() != null ? persona.getFeatures().dimensions().height : 2f) + 1f;
        try {
            ClientRenderUtils.drawEntity(x + 46, y + 95, (int)(60f / divider), 0, 0, persona);
        } catch (Exception e) {
            this.persona.setAppearance(null);
            this.persona.setFeatures(null);
            checkNextButton();
            e.printStackTrace();
        }
        matrices.popPose();

        for (ItemStack itemsTooltip : itemsTooltips) {
            renderTooltip(matrices, itemsTooltip, mouseX, mouseY);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public <T extends Widget & NarratableEntry & GuiEventListener> T addElement(T element) {
        return addWidget(addRenderableWidget(element));
    }

    @Override
    public void renderTooltip(ItemStack stack) {
        itemsTooltips.add(stack);
    }

    @Override
    public Font getText() {
        return this.font;
    }

    public Appearance<?> createAppearance() {
        return switch (this.mode) {
            case NPC -> this.persona.getAppearance() instanceof NpcAppearance gecko ? gecko : null;
            case GECKOLIB -> this.persona.getAppearance() instanceof GeckoLibAppearance gecko ? gecko : null;
            case ENTITY -> this.persona.getAppearance() instanceof EntityAppearance gecko ? gecko : null;
        };
    }

    public Features createFeatures() {
        EntityDimensions dimensions = this.persona.getFeatures() != null ? this.persona.getFeatures().dimensions() : EntityDimensions.scalable(0.6F, 1.8F);
        return new Features(this.facePlayer, this.baby, this.sitting, dimensions, this.nametag);
    }
}
