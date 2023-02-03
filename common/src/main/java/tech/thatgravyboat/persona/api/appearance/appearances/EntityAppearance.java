package tech.thatgravyboat.persona.api.appearance.appearances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import tech.thatgravyboat.persona.api.appearance.Appearance;
import tech.thatgravyboat.persona.api.appearance.AppearanceSerializer;

public record EntityAppearance(EntityType<?> entity) implements Appearance<EntityAppearance> {

    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public AppearanceSerializer<EntityAppearance> serializer() {
        return SERIALIZER;
    }

    static class Serializer implements AppearanceSerializer<EntityAppearance> {

        public static final Codec<EntityAppearance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Registry.ENTITY_TYPE.byNameCodec().fieldOf("entity").forGetter(EntityAppearance::entity)
        ).apply(instance, EntityAppearance::new));

        @Override
        public String id() {
            return "entity";
        }

        @Override
        public Codec<EntityAppearance> codec() {
            return CODEC;
        }
    }
}
