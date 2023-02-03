package tech.thatgravyboat.persona.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import tech.thatgravyboat.persona.client.renderer.PersonaRenderer;
import tech.thatgravyboat.persona.common.registry.Registry;

import java.util.function.Supplier;

public class PersonasClient {

    public static void init() {
        registerEntityRenderer(Registry.PERSONA, PersonaRenderer::new);
    }

    @ExpectPlatform
    public static <T extends Entity> void registerEntityRenderer(Supplier<EntityType<T>> entity, EntityRendererProvider<T> factory) {
        throw new AssertionError();
    }
}
