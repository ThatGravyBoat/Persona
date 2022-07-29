package tech.thatgravyboat.persona.api.interactions.types;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.MessageType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import tech.thatgravyboat.persona.api.CodecUtils;
import tech.thatgravyboat.persona.api.interactions.Interaction;
import tech.thatgravyboat.persona.api.interactions.InteractionType;
import tech.thatgravyboat.persona.api.interactions.types.base.InteractionSerializer;
import tech.thatgravyboat.persona.common.entity.Persona;

public record ChatInteraction(MutableText component) implements Interaction<ChatInteraction> {

    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public InteractionType type() {
        return InteractionType.CHAT;
    }

    @Override
    public void activate(Persona persona, ServerPlayerEntity player) {
        player.sendMessage(component, MessageType.CHAT, Util.NIL_UUID);
    }

    @Override
    public InteractionSerializer<ChatInteraction> serializer() {
        return SERIALIZER;
    }

    private static class Serializer implements InteractionSerializer<ChatInteraction> {

        public static final Codec<ChatInteraction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CodecUtils.passthrough(Text.Serializer::toJsonTree, Text.Serializer::fromJson).fieldOf("message").forGetter(ChatInteraction::component)
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
