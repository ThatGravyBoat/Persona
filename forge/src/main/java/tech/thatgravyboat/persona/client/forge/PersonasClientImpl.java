package tech.thatgravyboat.persona.client.forge;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import java.util.function.Supplier;

public class PersonasClientImpl {
    public static <T extends Entity> void registerEntityRenderer(Supplier<EntityType<T>> entity, EntityRendererFactory<T> factory) {
        EntityRenderers.register(entity.get(), factory);
    }
}
