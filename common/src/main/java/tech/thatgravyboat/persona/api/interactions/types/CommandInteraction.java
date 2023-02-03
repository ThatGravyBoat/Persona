package tech.thatgravyboat.persona.api.interactions.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.CommandSource;
import net.minecraft.server.level.ServerPlayer;
import tech.thatgravyboat.persona.api.interactions.Interaction;
import tech.thatgravyboat.persona.api.interactions.InteractionType;
import tech.thatgravyboat.persona.api.interactions.types.base.InteractionSerializer;
import tech.thatgravyboat.persona.common.entity.Persona;

public record CommandInteraction(String command) implements Interaction<CommandInteraction> {

    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public InteractionType type() {
        return InteractionType.COMMAND;
    }

    @Override
    public void activate(Persona persona, ServerPlayer player) {
        player.server.getCommands().performPrefixedCommand(player.createCommandSourceStack().withPermission(2).withSource(CommandSource.NULL), command);
    }

    @Override
    public InteractionSerializer<CommandInteraction> serializer() {
        return SERIALIZER;
    }

    private static class Serializer implements InteractionSerializer<CommandInteraction> {

        public static final Codec<CommandInteraction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("command").forGetter(CommandInteraction::command)
        ).apply(instance, CommandInteraction::new));

        @Override
        public String id() {
            return "command";
        }

        @Override
        public Codec<CommandInteraction> codec() {
            return CODEC;
        }
    }
}
