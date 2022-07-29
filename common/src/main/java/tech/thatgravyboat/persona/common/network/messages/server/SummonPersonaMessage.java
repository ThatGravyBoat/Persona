package tech.thatgravyboat.persona.common.network.messages.server;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.api.NpcData;
import tech.thatgravyboat.persona.common.entity.Persona;
import tech.thatgravyboat.persona.common.management.IPersonaHolder;
import tech.thatgravyboat.persona.common.network.handlers.IPacket;
import tech.thatgravyboat.persona.common.network.handlers.IPacketHandler;
import tech.thatgravyboat.persona.common.registry.Registry;

import java.util.function.Consumer;

public record SummonPersonaMessage(String id, BlockPos pos) implements IPacket<SummonPersonaMessage> {

    public static final Handler HANDLER = new Handler();
    public static final Identifier ID = new Identifier(Personas.MOD_ID, "summon_persona");

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public IPacketHandler<SummonPersonaMessage> getHandler() {
        return HANDLER;
    }

    private static class Handler implements IPacketHandler<SummonPersonaMessage> {

        @Override
        public void encode(SummonPersonaMessage message, PacketByteBuf buffer) {
            buffer.writeString(message.id());
            buffer.writeBlockPos(message.pos());
        }

        @Override
        public SummonPersonaMessage decode(PacketByteBuf buffer) {
            return new SummonPersonaMessage(buffer.readString(), buffer.readBlockPos());
        }

        @Override
        public Consumer<PlayerEntity> handle(SummonPersonaMessage message) {
            return player -> {
                if (player instanceof ServerPlayerEntity serverPlayer && serverPlayer.isCreativeLevelTwoOp()) {
                    if (serverPlayer.server instanceof IPersonaHolder holder && holder.getPersonaManager() != null) {
                        NpcData data = holder.getPersonaManager().getNpc(message.id());
                        if (data == null) {
                            player.sendMessage(Text.of("Could not find persona."), true);
                            return;
                        }
                        Persona persona = Registry.PERSONA.get().create(serverPlayer.world);
                        if (persona != null) {
                            persona.setPersona(data);
                            persona.setPos(message.pos().getX() + 0.5, message.pos().getY(), message.pos().getZ() + 0.5);
                            serverPlayer.world.spawnEntity(persona);
                        }
                    }
                }
            };
        }
    }
}

