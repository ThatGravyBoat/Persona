package tech.thatgravyboat.persona.api.appearance.appearances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;
import tech.thatgravyboat.persona.api.appearance.Appearance;
import tech.thatgravyboat.persona.api.appearance.AppearanceSerializer;
import tech.thatgravyboat.persona.client.renderer.GeckoGeoModel;

import java.util.Objects;
import java.util.Optional;

public final class GeckoLibAppearance implements Appearance<GeckoLibAppearance>, IAnimatable {

    public static final Serializer SERIALIZER = new Serializer();

    static {
        //noinspection unchecked
        AnimationController.addModelFetcher((IAnimatable object) -> object instanceof GeckoLibAppearance ? ((GeckoLibAppearance) object).getGeckoModel() : null);
    }

    private final ResourceLocation model;
    private final ResourceLocation texture;
    private final ResourceLocation animation;
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private final GeckoGeoModel<GeckoLibAppearance> geckoModel = new GeckoGeoModel<>();

    public GeckoLibAppearance(ResourceLocation model, ResourceLocation texture, Optional<ResourceLocation> animation) {
        this.model = model;
        this.texture = texture;
        this.animation = animation.orElse(null);
    }

    public GeckoLibAppearance(ResourceLocation model, ResourceLocation texture, ResourceLocation animation) {
        this.model = model;
        this.texture = texture;
        this.animation = animation;
    }

    @Override
    public AppearanceSerializer<GeckoLibAppearance> serializer() {
        return SERIALIZER;
    }

    @Override
    public void registerControllers(AnimationData data) {
        if (this.animation != null) {
            data.addAnimationController(new AnimationController<>(this, "controller", 0, event -> {
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.npc.idle", ILoopType.EDefaultLoopTypes.LOOP));
                return PlayState.CONTINUE;
            }));
        }
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    public ResourceLocation model() {
        return model;
    }

    public ResourceLocation texture() {
        return texture;
    }

    public Optional<ResourceLocation> animation() {
        return Optional.ofNullable(animation);
    }

    @SuppressWarnings("rawtypes")
    public GeckoGeoModel getGeckoModel() {
        return this.geckoModel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(model, texture, animation);
    }

    static class Serializer implements AppearanceSerializer<GeckoLibAppearance> {

        public static final Codec<GeckoLibAppearance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("model").forGetter(GeckoLibAppearance::model),
                ResourceLocation.CODEC.fieldOf("texture").forGetter(GeckoLibAppearance::texture),
                ResourceLocation.CODEC.optionalFieldOf("animation").forGetter(GeckoLibAppearance::animation)
        ).apply(instance, GeckoLibAppearance::new));

        @Override
        public String id() {
            return "geckolib";
        }

        @Override
        public Codec<GeckoLibAppearance> codec() {
            return CODEC;
        }
    }
}
