package tech.thatgravyboat.persona.api.interactions.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.network.ServerPlayerEntity;
import tech.thatgravyboat.persona.api.interactions.Interaction;
import tech.thatgravyboat.persona.api.interactions.InteractionType;
import tech.thatgravyboat.persona.api.interactions.types.base.InteractionSerializer;
import tech.thatgravyboat.persona.common.entity.Persona;

public record NothingInteraction() implements Interaction<NothingInteraction> {

    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public InteractionType type() {
        return InteractionType.NOTHING;
    }

    @Override
    public void activate(Persona persona, ServerPlayerEntity player) {}

    @Override
    public InteractionSerializer<NothingInteraction> serializer() {
        return SERIALIZER;
    }

    private static class Serializer implements InteractionSerializer<NothingInteraction> {

        public static final Codec<NothingInteraction> CODEC = MapCodec.of(Encoder.empty(), Decoder.unit(new NothingInteraction())).codec();

        @Override
        public String id() {
            return "nothing";
        }

        @Override
        public Codec<NothingInteraction> codec() {
            return CODEC;
        }
    }
}
