package tech.thatgravyboat.persona.client.renderer;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import tech.thatgravyboat.persona.api.appearance.appearances.GeckoLibAppearance;

public class GeckoGeoModel<T extends GeckoLibAppearance> extends AnimatedGeoModel<T> {

    public GeoModel getModel(T object) {
        return this.getModel(this.getModelResource(object));
    }

    @Override
    public ResourceLocation getModelResource(T object) {
        return object.model();
    }

    @Override
    public ResourceLocation getTextureResource(T object) {
        return object.texture();
    }

    @Override
    public ResourceLocation getAnimationResource(T object) {
        return object.animation().orElse(new ResourceLocation("persona","animations/empty.animation.json"));
    }
}
