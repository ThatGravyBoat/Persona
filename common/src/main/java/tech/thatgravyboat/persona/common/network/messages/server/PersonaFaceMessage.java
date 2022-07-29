package tech.thatgravyboat.persona.common.network.messages.server;

import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.common.lib.State;
import tech.thatgravyboat.persona.common.management.IPersonaHolder;
import tech.thatgravyboat.persona.common.network.NetPacketHandler;
import tech.thatgravyboat.persona.common.network.handlers.IPacket;
import tech.thatgravyboat.persona.common.network.handlers.IPacketHandler;
import tech.thatgravyboat.persona.common.network.messages.client.IdStateMessage;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public record PersonaFaceMessage(int mob) implements IPacket<PersonaFaceMessage> {

    public static final Handler HANDLER = new Handler();
    public static final Identifier ID = new Identifier(Personas.MOD_ID, "face_me");



    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public IPacketHandler<PersonaFaceMessage> getHandler() {
        return HANDLER;
    }

    private static class Handler implements IPacketHandler<PersonaFaceMessage> {

        @Override
        public void encode(PersonaFaceMessage message, PacketByteBuf buffer) {
            buffer.writeVarInt(message.mob);
        }

        @Override
        public PersonaFaceMessage decode(PacketByteBuf buffer) {
            return new PersonaFaceMessage(buffer.readVarInt());
        }

        @Override
        public Consumer<PlayerEntity> handle(PersonaFaceMessage message) {
            return player -> {
                if (player instanceof ServerPlayerEntity serverPlayer && serverPlayer.isCreativeLevelTwoOp()) {
                    Entity npc = serverPlayer.world.getEntityById(message.mob());
                    if (npc != null) {
                        npc.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, player.getPos());
                    }
                }
            };
        }
    }
}
