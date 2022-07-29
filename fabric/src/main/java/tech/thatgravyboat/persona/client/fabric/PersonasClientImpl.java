package tech.thatgravyboat.persona.client.fabric;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import java.util.function.Supplier;

public class PersonasClientImpl {

    public static <T extends Entity> void registerEntityRenderer(Supplier<EntityType<T>> entity, EntityRendererFactory<T> factory) {
        EntityRendererRegistry.register(entity.get(), factory);
    }
}
