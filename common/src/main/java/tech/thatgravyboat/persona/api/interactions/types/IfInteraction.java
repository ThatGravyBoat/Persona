package tech.thatgravyboat.persona.api.interactions.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.network.ServerPlayerEntity;
import tech.thatgravyboat.persona.api.conditions.Conditions;
import tech.thatgravyboat.persona.api.conditions.types.base.Condition;
import tech.thatgravyboat.persona.api.interactions.Interaction;
import tech.thatgravyboat.persona.api.interactions.InteractionType;
import tech.thatgravyboat.persona.api.interactions.Interactions;
import tech.thatgravyboat.persona.api.interactions.types.base.InteractionSerializer;
import tech.thatgravyboat.persona.common.entity.Persona;

import java.util.Optional;

public record IfInteraction(
        Condition<?> condition,
        Interaction<?> interaction,
        Optional<Interaction<?>> elseInteraction
) implements Interaction<IfInteraction> {

    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public InteractionType type() {
        return InteractionType.IF;
    }

    @Override
    public void activate(Persona persona, ServerPlayerEntity player) {
        if (condition().valid(persona, player)) {
            interaction().activate(persona, player);
        } else if (elseInteraction().isPresent()) {
            elseInteraction().get().activate(persona, player);
        }
    }

    @Override
    public InteractionSerializer<IfInteraction> serializer() {
        return SERIALIZER;
    }

    private static class Serializer implements InteractionSerializer<IfInteraction> {

        public static final Codec<IfInteraction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Conditions.CODEC.fieldOf("condition").forGetter(IfInteraction::condition),
                Interactions.CODEC.fieldOf("interaction").forGetter(IfInteraction::interaction),
                Interactions.CODEC.optionalFieldOf("else").forGetter(IfInteraction::elseInteraction)
        ).apply(instance, IfInteraction::new));

        @Override
        public String id() {
            return "if";
        }

        @Override
        public Codec<IfInteraction> codec() {
            return CODEC;
        }
    }
}
