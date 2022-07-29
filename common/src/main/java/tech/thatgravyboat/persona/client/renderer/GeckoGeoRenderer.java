package tech.thatgravyboat.persona.client.renderer;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;
import tech.thatgravyboat.persona.api.appearance.appearances.GeckoLibAppearance;

public class GeckoGeoRenderer implements IGeoRenderer<GeckoLibAppearance> {
    @Override
    public GeoModelProvider getGeoModelProvider() {
        return null;
    }

    @Override
    public Identifier getTextureLocation(GeckoLibAppearance instance) {
        return instance.texture();
    }
}
