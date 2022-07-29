package tech.thatgravyboat.persona.client.screens.appearance;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
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

import java.util.*;
import java.util.regex.Pattern;

public class AppearanceScreen extends Screen implements ScreenHelper {

    private static final Identifier BACKGROUND = new Identifier(Personas.MOD_ID, "textures/appearance.png");

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

    private ButtonWidget nextPage;

    public AppearanceScreen(String id, BlockPos pos) {
        super(new LiteralText(""));
        this.persona = new Persona(Registry.PERSONA.get(), MinecraftClient.getInstance().world, false);
        persona.setCustomName(new LiteralText(this.displayName));
        this.id = id;
        this.pos = pos;
    }

    public void updateScreen() {
        clearChildren();
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
                b -> client.setScreen(new InteractionScreen(this.id, this.displayName, this.pos, createAppearance(), createFeatures()))));

        addElement(new ChangeModeWidget(mode,x + 204, y + 8, 20, 20, m -> {
            this.mode = m;
            updateScreen();
        }, (button, matrices, mouseX, mouseY) -> this.renderTooltip(matrices, "Mode: " + this.mode.name().toLowerCase(Locale.ROOT), mouseX, mouseY)));

        TextFieldWidget text = addElement(new TextFieldWidget(this.textRenderer, x + 9, y + 9, 73, 10, new LiteralText("")));
        text.setDrawsBackground(false);
        text.setChangedListener(s -> {
            this.displayName = s;
            persona.setCustomName(new LiteralText(this.displayName));
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
                var labelTextField = addElement(new LabelTextField(this.textRenderer, x + 88, y + 20, 110, 10, new LiteralText("Username:")));
                labelTextField.setMaxLength(16);
                ButtonWidget search = addElement(new ButtonWidget(x + 88, y + 32, 110, 20, new LiteralText("Search"), p -> {
                    var skin = SkinHelper.getSkin(labelTextField.getText());
                    if (skin != null) {
                        persona.setAppearance(new NpcAppearance(skin.getLeft(), skin.getRight()));
                        updateFeatures(EntityDimensions.changing(0.6F, 1.8F));
                        checkNextButton();
                    }
                }));
                search.active = false;
                labelTextField.setChangedListener(s -> {
                    boolean valid = VALID.matcher(s).matches() && s.length() < 17 && s.length() > 2;
                    labelTextField.setEditableColor(valid ? 0xffffff : 0xff0000);
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
                        EntityType<?> entityType = spawnEggItem.getEntityType(null);
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
                var modelText = addElement(new LabelTextField(this.textRenderer, x + 88, y + 20, 110, 10, new LiteralText("Model:")));
                var textureText = addElement(new LabelTextField(this.textRenderer, x + 88, y + 42, 110, 10, new LiteralText("Texture:")));
                var animation = addElement(new LabelTextField(this.textRenderer, x + 88, y + 64, 110, 10, new LiteralText("Animation (Optional):")));

                modelText.setMaxLength(1000);
                textureText.setMaxLength(1000);
                animation.setMaxLength(1000);

                ButtonWidget preview = addElement(new ButtonWidget(x + 88, y + 76, 110, 20, new LiteralText("Preview"), p -> {
                    if (isGeckoGood(modelText, textureText, animation)) {
                        persona.setAppearance(new GeckoLibAppearance(getId(modelText), getId(textureText), getId(animation)));
                        updateFeatures(EntityDimensions.changing(0.6F, 1.8F));
                        checkNextButton();
                    }
                }));
                preview.active = false;

                modelText.setChangedListener(s -> {
                    Identifier identifier = Identifier.tryParse(s);
                    boolean isGood = identifier != null && identifier.getPath().endsWith(".geo.json") && identifier.getPath().startsWith("geo/");
                    modelText.setEditableColor(isGood ? 0xffffff : 0xff0000);
                    preview.active = isGeckoGood(modelText, textureText, animation);
                });
                textureText.setChangedListener(s -> {
                    Identifier identifier = Identifier.tryParse(s);
                    boolean isGood = identifier != null && identifier.getPath().endsWith(".png") && identifier.getPath().startsWith("textures/entity/");
                    textureText.setEditableColor(isGood ? 0xffffff : 0xff0000);
                    preview.active = isGeckoGood(modelText, textureText, animation);
                });
                animation.setChangedListener(s -> {
                    if (!Objects.equals(s, "")) {
                        Identifier identifier = Identifier.tryParse(s);
                        boolean isGood = identifier != null && identifier.getPath().endsWith(".animation.json") && identifier.getPath().startsWith("animations/");
                        animation.setEditableColor(isGood ? 0xffffff : 0xff0000);
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

    private Identifier getId(TextFieldWidget field) {
        return Identifier.tryParse(field.getText());
    }

    private boolean isGeckoGood(TextFieldWidget model, TextFieldWidget texture, TextFieldWidget animation) {
        Identifier identifier = Identifier.tryParse(model.getText());
        boolean modelGood = identifier != null && identifier.getPath().endsWith(".geo.json") && identifier.getPath().startsWith("geo/");
        identifier = Identifier.tryParse(texture.getText());
        boolean textureGood = identifier != null && identifier.getPath().endsWith(".png") && identifier.getPath().startsWith("textures/entity/");
        identifier = Identifier.tryParse(animation.getText());
        boolean animationGood = animation.getText().equals("") || (identifier != null && identifier.getPath().endsWith(".animation.json") && identifier.getPath().startsWith("animations/"));
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
                persona.getFeatures() != null ? persona.getFeatures().dimensions() : EntityDimensions.changing(1, 2);
        persona.setFeatures(new Features(false, this.baby, this.sitting, entityDimensions, this.nametag));
    }

    private void renderTooltip(MatrixStack stack, String text, int x, int y) {
        this.renderTooltip(stack, new LiteralText(text), x, y);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        itemsTooltips.clear();
        renderBackground(matrices);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int x = this.width / 2 - 116;
        int y = this.height / 2 - 64;

        drawTexture(matrices, x, this.height / 2 - 64, 0,0, 232, 128);
        super.render(matrices, mouseX, mouseY, delta);

        matrices.push();

        float divider = Math.max(2f, persona.getFeatures() != null ? persona.getFeatures().dimensions().height : 2f) + 1f;
        try {
            ClientRenderUtils.drawEntity(x + 46, y + 95, (int)(60f / divider), 0, 0, persona);
        } catch (Exception e) {
            this.persona.setAppearance(null);
            this.persona.setFeatures(null);
            checkNextButton();
            e.printStackTrace();
        }
        matrices.pop();

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

    @Override
    public void renderTooltip(ItemStack stack) {
        itemsTooltips.add(stack);
    }

    @Override
    public TextRenderer getText() {
        return this.textRenderer;
    }

    public Appearance<?> createAppearance() {
        return switch (this.mode) {
            case NPC -> this.persona.getAppearance() instanceof NpcAppearance gecko ? gecko : null;
            case GECKOLIB -> this.persona.getAppearance() instanceof GeckoLibAppearance gecko ? gecko : null;
            case ENTITY -> this.persona.getAppearance() instanceof EntityAppearance gecko ? gecko : null;
        };
    }

    public Features createFeatures() {
        EntityDimensions dimensions = this.persona.getFeatures() != null ? this.persona.getFeatures().dimensions() : EntityDimensions.changing(0.6F, 1.8F);
        return new Features(this.facePlayer, this.baby, this.sitting, dimensions, this.nametag);
    }
}
