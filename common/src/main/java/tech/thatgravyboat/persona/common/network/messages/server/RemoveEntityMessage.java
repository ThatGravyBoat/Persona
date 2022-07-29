package tech.thatgravyboat.persona.common.network.messages.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.common.entity.PersonaSpawnEgg;
import tech.thatgravyboat.persona.common.network.handlers.IPacket;
import tech.thatgravyboat.persona.common.network.handlers.IPacketHandler;

import java.util.function.Consumer;

public record RemoveEntityMessage(int mob) implements IPacket<RemoveEntityMessage> {

    public static final Handler HANDLER = new Handler();
    public static final Identifier ID = new Identifier(Personas.MOD_ID, "remove_entity");

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public IPacketHandler<RemoveEntityMessage> getHandler() {
        return HANDLER;
    }

    private static class Handler implements IPacketHandler<RemoveEntityMessage> {

        @Override
        public void encode(RemoveEntityMessage message, PacketByteBuf buffer) {
            buffer.writeVarInt(message.mob);
        }

        @Override
        public RemoveEntityMessage decode(PacketByteBuf buffer) {
            return new RemoveEntityMessage(buffer.readVarInt());
        }

        @Override
        public Consumer<PlayerEntity> handle(RemoveEntityMessage message) {
            return player -> {
                if (player instanceof ServerPlayerEntity serverPlayer && serverPlayer.isCreativeLevelTwoOp()) {
                    Entity npc = serverPlayer.world.getEntityById(message.mob());
                    if (npc != null) {
                        npc.discard();
                    }
                }
            };
        }
    }
}
