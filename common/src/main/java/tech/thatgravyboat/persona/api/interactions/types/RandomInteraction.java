package tech.thatgravyboat.persona.api.interactions.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import tech.thatgravyboat.persona.api.interactions.Interaction;
import tech.thatgravyboat.persona.api.interactions.InteractionType;
import tech.thatgravyboat.persona.api.interactions.Interactions;
import tech.thatgravyboat.persona.api.interactions.types.base.InteractionSerializer;
import tech.thatgravyboat.persona.common.entity.Persona;

import java.util.List;

public record RandomInteraction(List<Interaction<?>> interactions) implements Interaction<RandomInteraction> {

    public static final Serializer SERIALIZER = new Serializer();

    public RandomInteraction {
        Interaction.checkLists(interactions);
    }

    @Override
    public InteractionType type() {
        return InteractionType.RANDOM;
    }

    @Override
    public void activate(Persona persona, ServerPlayer player) {
        if (interactions.isEmpty()) return;
        interactions.get(player.getRandom().nextInt(interactions.size())).activate(persona, player);
    }

    @Override
    public InteractionSerializer<RandomInteraction> serializer() {
        return SERIALIZER;
    }

    private static class Serializer implements InteractionSerializer<RandomInteraction> {

        public static final Codec<RandomInteraction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Interactions.CODEC.listOf().fieldOf("interactions").forGetter(RandomInteraction::interactions)
        ).apply(instance, RandomInteraction::new));
        @Override
        public String id() {
            return "random";
        }

        @Override
        public Codec<RandomInteraction> codec() {
            return CODEC;
        }
    }
}
