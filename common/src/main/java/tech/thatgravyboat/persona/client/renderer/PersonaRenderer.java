package tech.thatgravyboat.persona.client.renderer;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import tech.thatgravyboat.persona.api.appearance.Appearance;
import tech.thatgravyboat.persona.api.appearance.appearances.EntityAppearance;
import tech.thatgravyboat.persona.api.appearance.appearances.GeckoLibAppearance;
import tech.thatgravyboat.persona.api.appearance.appearances.NpcAppearance;
import tech.thatgravyboat.persona.common.entity.Persona;

import java.util.Collections;

public class PersonaRenderer extends EntityRenderer<Persona> {

    private final PlayerModel<Player> slimModel;
    private final PlayerModel<Player> normalModel;
    private final GeckoGeoRenderer geckoGeoRenderer = new GeckoGeoRenderer();

    public PersonaRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        slimModel = new CustomPlayerModel<>(ctx.getModelSet().bakeLayer(ModelLayers.PLAYER_SLIM), true);
        normalModel = new CustomPlayerModel<>(ctx.getModelSet().bakeLayer(ModelLayers.PLAYER), false);
    }

    @Override
    public void render(Persona entity, float yaw, float tickDelta, PoseStack stack, MultiBufferSource vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, stack, vertexConsumers, light);
        stack.pushPose();
        Minecraft client = Minecraft.getInstance();
        Entity player = client.cameraEntity;
        if (player == null) return;
        var facePlayer = entity.getFeatures() != null && entity.getFeatures().shouldFacePlayer();
        double zDifference = player.getZ() - entity.getZ();
        double xDifference = player.getX() - entity.getX();
        float newYaw = Mth.wrapDegrees((float)(Mth.atan2(zDifference, xDifference) * 57.2957763671875D) - 90.0F);
        stack.mulPose(facePlayer ? Vector3f.YN.rotationDegrees(newYaw) : Vector3f.YP.rotationDegrees(180.0F - yaw));
        Appearance<?> appearance = entity.getAppearance();

        boolean isGeckoAndUi = appearance instanceof GeckoLibAppearance && entity.isInGui();
        boolean isNotFacingOrInUI = !(appearance instanceof GeckoLibAppearance) && !facePlayer && !entity.isInGui();

        if (isGeckoAndUi || isNotFacingOrInUI) {
            stack.mulPose(Vector3f.YP.rotationDegrees(180));
        }



        if (appearance instanceof NpcAppearance npcAppearance) {
            ResourceLocation skin = client.getSkinManager().registerTexture(new MinecraftProfileTexture(npcAppearance.skin(), null), MinecraftProfileTexture.Type.SKIN);
            stack.translate(0.0D, 1.5d, 0.0D);
            stack.mulPose(Vector3f.XN.rotationDegrees(180));
            PlayerModel<Player> playerModel = npcAppearance.slim() ? slimModel : normalModel;
            playerModel.young = entity.getFeatures() != null && entity.getFeatures().child();
            editModel(playerModel, entity.getFeatures() != null && entity.getFeatures().sit());
            if (entity.getFeatures() != null && entity.getFeatures().sit()) {
                stack.translate(0d, 0.6d, 0d);
            }
            playerModel.renderToBuffer(stack, vertexConsumers.getBuffer(RenderType.entityTranslucent(skin)), light, OverlayTexture.pack(0, false), 1f, 1f, 1f, 1f);
        }

        if (appearance instanceof GeckoLibAppearance geckoLibAppearance) {

            RenderType entityCutout = RenderType.entityTranslucent(geckoLibAppearance.texture());
            VertexConsumer ivertexbuilder = vertexConsumers.getBuffer(entityCutout);

            //noinspection rawtypes
            GeckoGeoModel geckoModel = geckoLibAppearance.getGeckoModel();
            //noinspection unchecked
            GeoModel model = geckoModel.getModel(geckoLibAppearance);
            AnimationEvent<GeckoLibAppearance> event = new AnimationEvent<>(geckoLibAppearance, 0, 0, Minecraft.getInstance().getFrameTime(), false, Collections.emptyList());
            //noinspection unchecked
            geckoModel.setCustomAnimations(geckoLibAppearance, geckoGeoRenderer.getInstanceId(geckoLibAppearance), event);
            geckoGeoRenderer.render(model, geckoLibAppearance,
                    tickDelta,
                    entityCutout, stack, vertexConsumers, ivertexbuilder, light, OverlayTexture.NO_OVERLAY,
                    1f, 1f, 1f, 1f);
        }
        if (appearance instanceof EntityAppearance entityAppearance) {
            Entity clientEntity = entity.getClientEntity(entity.level, entityAppearance.entity());
            if (clientEntity instanceof Mob mob) {
                mob.setBaby(entity.getFeatures() != null && entity.getFeatures().child());
            }
            this.entityRenderDispatcher.render(clientEntity, 0, 0, 0, 0f, 0f, stack, vertexConsumers, light);
        }
        stack.popPose();
    }

    private void editModel(PlayerModel<Player> model, boolean sitting) {
        if (sitting) {
            model.rightArm.xRot = -0.62831855F;
            model.leftArm.xRot = -0.62831855F;
            model.rightLeg.xRot = -1.4137167F;
            model.rightLeg.yRot = 0.31415927F;
            model.rightLeg.zRot = 0.07853982F;
            model.leftLeg.xRot = -1.4137167F;
            model.leftLeg.yRot = -0.31415927F;
            model.leftLeg.zRot = -0.07853982F;
        } else {
            model.rightArm.xRot = 0f;
            model.leftArm.xRot = 0f;
            model.rightLeg.xRot = 0f;
            model.rightLeg.yRot = 0f;
            model.rightLeg.zRot = 0f;
            model.leftLeg.xRot = 0f;
            model.leftLeg.yRot = 0f;
            model.leftLeg.zRot = 0f;
        }
        model.leftPants.copyFrom(model.leftLeg);
        model.rightPants.copyFrom(model.rightLeg);
        model.leftSleeve.copyFrom(model.leftArm);
        model.rightSleeve.copyFrom(model.rightArm);
        model.jacket.copyFrom(model.body);
        model.hat.copyFrom(model.head);
    }

    @Override
    protected boolean shouldShowName(Persona entity) {
        return (entity.getFeatures() == null || entity.getFeatures().nameTag());
    }

    @Override
    public ResourceLocation getTextureLocation(Persona entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
