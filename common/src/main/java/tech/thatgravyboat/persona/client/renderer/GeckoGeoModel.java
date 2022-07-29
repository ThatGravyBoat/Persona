package tech.thatgravyboat.persona.client.renderer;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import tech.thatgravyboat.persona.api.appearance.appearances.GeckoLibAppearance;

public class GeckoGeoModel<T extends GeckoLibAppearance> extends AnimatedGeoModel<T> {

    public GeoModel getModel(T object) {
        return this.getModel(this.getModelLocation(object));
    }

    @Override
    public Identifier getModelLocation(T object) {
        return object.model();
    }

    @Override
    public Identifier getTextureLocation(T object) {
        return object.texture();
    }

    @Override
    public Identifier getAnimationFileLocation(T object) {
        return object.animation().orElse(new Identifier("persona","animations/empty.animation.json"));
    }
}
