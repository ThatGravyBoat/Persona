package tech.thatgravyboat.persona.client.renderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.provider.GeoModelProvider;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;
import tech.thatgravyboat.persona.api.appearance.appearances.GeckoLibAppearance;

public class GeckoGeoRenderer implements IGeoRenderer<GeckoLibAppearance> {

    private MultiBufferSource bufferSource;

    @Override
    public MultiBufferSource getCurrentRTB() {
        return bufferSource;
    }

    @Override
    public void setCurrentRTB(MultiBufferSource bufferSource) {
        this.bufferSource = bufferSource;
    }

    @Override
    public GeoModelProvider getGeoModelProvider() {
        return null;
    }

    @Override
    public ResourceLocation getTextureLocation(GeckoLibAppearance instance) {
        return instance.texture();
    }

    @Override
    public ResourceLocation getTextureResource(GeckoLibAppearance instance) {
        return instance.texture();
    }
}
