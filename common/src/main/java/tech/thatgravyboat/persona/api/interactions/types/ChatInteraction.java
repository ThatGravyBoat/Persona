package tech.thatgravyboat.persona.api.interactions.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import tech.thatgravyboat.persona.api.CodecUtils;
import tech.thatgravyboat.persona.api.interactions.Interaction;
import tech.thatgravyboat.persona.api.interactions.InteractionType;
import tech.thatgravyboat.persona.api.interactions.types.base.InteractionSerializer;
import tech.thatgravyboat.persona.common.entity.Persona;

public record ChatInteraction(MutableComponent component) implements Interaction<ChatInteraction> {

    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public InteractionType type() {
        return InteractionType.CHAT;
    }

    @Override
    public void activate(Persona persona, ServerPlayer player) {
        player.sendSystemMessage(component);
    }

    @Override
    public InteractionSerializer<ChatInteraction> serializer() {
        return SERIALIZER;
    }

    private static class Serializer implements InteractionSerializer<ChatInteraction> {

        public static final Codec<ChatInteraction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CodecUtils.passthrough(Component.Serializer::toJsonTree, Component.Serializer::fromJson).fieldOf("message").forGetter(ChatInteraction::component)
        ).apply(instance, ChatInteraction::new));

        @Override
        public String id() {
            return "chat";
        }

        @Override
        public Codec<ChatInteraction> codec() {
            return CODEC;
        }
    }
}
