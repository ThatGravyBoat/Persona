package tech.thatgravyboat.persona.api.appearance.appearances;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import tech.thatgravyboat.persona.api.appearance.Appearance;
import tech.thatgravyboat.persona.api.appearance.AppearanceSerializer;

public record NpcAppearance(String skin, boolean slim) implements Appearance<NpcAppearance> {

    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public AppearanceSerializer<NpcAppearance> serializer() {
        return SERIALIZER;
    }

    static class Serializer implements AppearanceSerializer<NpcAppearance> {

        public static final Codec<NpcAppearance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("skin").forGetter(NpcAppearance::skin),
                Codec.BOOL.fieldOf("slim").orElse(false).forGetter(NpcAppearance::slim)
        ).apply(instance, NpcAppearance::new));

        @Override
        public String id() {
            return "npc";
        }

        @Override
        public Codec<NpcAppearance> codec() {
            return CODEC;
        }
    }
}
