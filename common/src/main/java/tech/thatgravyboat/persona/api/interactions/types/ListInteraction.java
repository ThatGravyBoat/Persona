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

public record ListInteraction(List<Interaction<?>> interactions) implements Interaction<ListInteraction> {

    public static final Serializer SERIALIZER = new Serializer();

    public ListInteraction {
        Interaction.checkLists(interactions);
    }

    @Override
    public InteractionType type() {
        return InteractionType.LIST;
    }

    @Override
    public void activate(Persona persona, ServerPlayer player) {
        interactions.forEach(i -> i.activate(persona, player));
    }

    @Override
    public InteractionSerializer<ListInteraction> serializer() {
        return SERIALIZER;
    }

    private static class Serializer implements InteractionSerializer<ListInteraction> {

        public static final Codec<ListInteraction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Interactions.CODEC.listOf().fieldOf("interactions").forGetter(ListInteraction::interactions)
        ).apply(instance, ListInteraction::new));

        @Override
        public String id() {
            return "list";
        }

        @Override
        public Codec<ListInteraction> codec() {
            return CODEC;
        }
    }
}
