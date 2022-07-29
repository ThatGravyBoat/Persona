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

public record SelectEntityMessage(int mob) implements IPacket<SelectEntityMessage> {

    public static final Handler HANDLER = new Handler();
    public static final Identifier ID = new Identifier(Personas.MOD_ID, "select_entity");

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public IPacketHandler<SelectEntityMessage> getHandler() {
        return HANDLER;
    }

    private static class Handler implements IPacketHandler<SelectEntityMessage> {

        @Override
        public void encode(SelectEntityMessage message, PacketByteBuf buffer) {
            buffer.writeVarInt(message.mob);
        }

        @Override
        public SelectEntityMessage decode(PacketByteBuf buffer) {
            return new SelectEntityMessage(buffer.readVarInt());
        }

        @Override
        public Consumer<PlayerEntity> handle(SelectEntityMessage message) {
            return player -> {
                if (player instanceof ServerPlayerEntity serverPlayer && serverPlayer.isCreativeLevelTwoOp()) {
                    Entity npc = serverPlayer.world.getEntityById(message.mob());
                    ItemStack stack = player.getMainHandStack();
                    if (npc != null && stack.getItem() instanceof PersonaSpawnEgg) {
                        stack.getOrCreateNbt().putInt("Entity", npc.getId());
                    }
                }
            };
        }
    }
}
