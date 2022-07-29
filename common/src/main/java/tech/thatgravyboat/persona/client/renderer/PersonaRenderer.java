package tech.thatgravyboat.persona.client.renderer;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import tech.thatgravyboat.persona.api.appearance.Appearance;
import tech.thatgravyboat.persona.api.appearance.appearances.EntityAppearance;
import tech.thatgravyboat.persona.api.appearance.appearances.GeckoLibAppearance;
import tech.thatgravyboat.persona.api.appearance.appearances.NpcAppearance;
import tech.thatgravyboat.persona.common.entity.Persona;

import java.util.Collections;

public class PersonaRenderer extends EntityRenderer<Persona> {

    private final PlayerEntityModel<PlayerEntity> slimModel;
    private final PlayerEntityModel<PlayerEntity> normalModel;
    private final GeckoGeoRenderer geckoGeoRenderer = new GeckoGeoRenderer();

    public PersonaRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        slimModel = new CustomPlayerModel<>(ctx.getPart(EntityModelLayers.PLAYER_SLIM), true);
        normalModel = new CustomPlayerModel<>(ctx.getPart(EntityModelLayers.PLAYER), false);
    }

    @Override
    public void render(Persona entity, float yaw, float tickDelta, MatrixStack stack, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, stack, vertexConsumers, light);
        stack.push();
        MinecraftClient client = MinecraftClient.getInstance();
        Entity player = client.cameraEntity;
        if (player == null) return;
        var facePlayer = entity.getFeatures() != null && entity.getFeatures().shouldFacePlayer();
        double zDifference = player.getZ() - entity.getZ();
        double xDifference = player.getX() - entity.getX();
        float newYaw = MathHelper.wrapDegrees((float)(MathHelper.atan2(zDifference, xDifference) * 57.2957763671875D) - 90.0F);
        stack.multiply(facePlayer ? Vec3f.NEGATIVE_Y.getDegreesQuaternion(newYaw) : Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F - yaw));
        Appearance<?> appearance = entity.getAppearance();

        boolean isGeckoAndUi = appearance instanceof GeckoLibAppearance && entity.isInGui();
        boolean isNotFacingOrInUI = !(appearance instanceof GeckoLibAppearance) && !facePlayer && !entity.isInGui();

        if (isGeckoAndUi || isNotFacingOrInUI) {
            stack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
        }



        if (appearance instanceof NpcAppearance npcAppearance) {
            Identifier skin = client.getSkinProvider().loadSkin(new MinecraftProfileTexture(npcAppearance.skin(), null), MinecraftProfileTexture.Type.SKIN);
            stack.translate(0.0D, 1.5d, 0.0D);
            stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
            PlayerEntityModel<PlayerEntity> playerModel = npcAppearance.slim() ? slimModel : normalModel;
            playerModel.child = entity.getFeatures() != null && entity.getFeatures().child();
            editModel(playerModel, entity.getFeatures() != null && entity.getFeatures().sit());
            if (entity.getFeatures() != null && entity.getFeatures().sit()) {
                stack.translate(0d, 0.6d, 0d);
            }
            playerModel.render(stack, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(skin)), light, OverlayTexture.getUv(0, false), 1f, 1f, 1f, 1f);
        }

        if (appearance instanceof GeckoLibAppearance geckoLibAppearance) {

            RenderLayer entityCutout = RenderLayer.getEntityTranslucent(geckoLibAppearance.texture());
            VertexConsumer ivertexbuilder = vertexConsumers.getBuffer(entityCutout);

            //noinspection rawtypes
            GeckoGeoModel geckoModel = geckoLibAppearance.getGeckoModel();
            //noinspection unchecked
            GeoModel model = geckoModel.getModel(geckoLibAppearance);
            AnimationEvent<GeckoLibAppearance> event = new AnimationEvent<>(geckoLibAppearance, 0, 0, MinecraftClient.getInstance().getTickDelta(), false, Collections.emptyList());
            //noinspection unchecked
            geckoModel.setLivingAnimations(geckoLibAppearance, geckoGeoRenderer.getUniqueID(geckoLibAppearance), event);
            geckoGeoRenderer.render(model, geckoLibAppearance,
                    tickDelta,
                    entityCutout, stack, vertexConsumers, ivertexbuilder, light, OverlayTexture.DEFAULT_UV,
                    1f, 1f, 1f, 1f);
        }
        if (appearance instanceof EntityAppearance entityAppearance) {
            Entity clientEntity = entity.getClientEntity(entity.world, entityAppearance.entity());
            if (clientEntity instanceof MobEntity mob) {
                mob.setBaby(entity.getFeatures() != null && entity.getFeatures().child());
            }
            this.dispatcher.render(clientEntity, 0, 0, 0, 0f, 0f, stack, vertexConsumers, light);
        }
        stack.pop();
    }

    private void editModel(PlayerEntityModel<PlayerEntity> model, boolean sitting) {
        if (sitting) {
            model.rightArm.pitch = -0.62831855F;
            model.leftArm.pitch = -0.62831855F;
            model.rightLeg.pitch = -1.4137167F;
            model.rightLeg.yaw = 0.31415927F;
            model.rightLeg.roll = 0.07853982F;
            model.leftLeg.pitch = -1.4137167F;
            model.leftLeg.yaw = -0.31415927F;
            model.leftLeg.roll = -0.07853982F;
        } else {
            model.rightArm.pitch = 0f;
            model.leftArm.pitch = 0f;
            model.rightLeg.pitch = 0f;
            model.rightLeg.yaw = 0f;
            model.rightLeg.roll = 0f;
            model.leftLeg.pitch = 0f;
            model.leftLeg.yaw = 0f;
            model.leftLeg.roll = 0f;
        }
        model.leftPants.copyTransform(model.leftLeg);
        model.rightPants.copyTransform(model.rightLeg);
        model.leftSleeve.copyTransform(model.leftArm);
        model.rightSleeve.copyTransform(model.rightArm);
        model.jacket.copyTransform(model.body);
        model.hat.copyTransform(model.head);
    }

    @Override
    protected boolean hasLabel(Persona entity) {
        return (entity.getFeatures() == null || entity.getFeatures().nameTag());
    }

    @Override
    public Identifier getTexture(Persona entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}
