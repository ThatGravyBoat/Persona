package tech.thatgravyboat.persona.api.interactions.types;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.network.ServerPlayerEntity;
import tech.thatgravyboat.persona.api.interactions.Interaction;
import tech.thatgravyboat.persona.api.interactions.InteractionType;
import tech.thatgravyboat.persona.api.interactions.Interactions;
import tech.thatgravyboat.persona.api.interactions.types.base.InteractionSerializer;
import tech.thatgravyboat.persona.common.entity.Persona;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public record DelayedInteraction(int delay, Interaction<?> interaction) implements Interaction<DelayedInteraction> {

    private static final AtomicInteger COUNTER = new AtomicInteger(0);
    private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(10,
            run -> new Thread(run, "Persona Delayed Interaction Thread " + COUNTER.incrementAndGet()));

    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public InteractionType type() {
        return InteractionType.DELAYED;
    }

    @Override
    public void activate(Persona persona, ServerPlayerEntity player) {
        EXECUTOR.schedule(() -> player.server.execute(() -> interaction.activate(persona, player)), delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public InteractionSerializer<DelayedInteraction> serializer() {
        return SERIALIZER;
    }

    private static class Serializer implements InteractionSerializer<DelayedInteraction> {

        public static final Codec<DelayedInteraction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("delay").forGetter(DelayedInteraction::delay),
                Interactions.CODEC.fieldOf("interaction").forGetter(DelayedInteraction::interaction)
        ).apply(instance, DelayedInteraction::new));

        @Override
        public String id() {
            return "delayed";
        }

        @Override
        public Codec<DelayedInteraction> codec() {
            return CODEC;
        }
    }
}
