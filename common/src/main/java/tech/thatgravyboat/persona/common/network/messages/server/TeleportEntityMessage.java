package tech.thatgravyboat.persona.common.network.messages.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.common.entity.PersonaSpawnEgg;
import tech.thatgravyboat.persona.common.network.handlers.IPacket;
import tech.thatgravyboat.persona.common.network.handlers.IPacketHandler;

import java.util.UUID;
import java.util.function.Consumer;

public record TeleportEntityMessage(Vec3d pos) implements IPacket<TeleportEntityMessage> {

    public static final Handler HANDLER = new Handler();
    public static final Identifier ID = new Identifier(Personas.MOD_ID, "teleport_entity");

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public IPacketHandler<TeleportEntityMessage> getHandler() {
        return HANDLER;
    }

    private static class Handler implements IPacketHandler<TeleportEntityMessage> {

        @Override
        public void encode(TeleportEntityMessage message, PacketByteBuf buffer) {
            buffer.writeDouble(message.pos.x);
            buffer.writeDouble(message.pos.y);
            buffer.writeDouble(message.pos.z);
        }

        @Override
        public TeleportEntityMessage decode(PacketByteBuf buffer) {
            return new TeleportEntityMessage(new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
        }

        @Override
        public Consumer<PlayerEntity> handle(TeleportEntityMessage message) {
            return player -> {
                if (player instanceof ServerPlayerEntity serverPlayer && serverPlayer.isCreativeLevelTwoOp()) {
                    ItemStack stack = player.getMainHandStack();
                    if (stack.getItem() instanceof PersonaSpawnEgg) {
                        NbtCompound tag = stack.getOrCreateNbt();
                        if (tag.contains("Entity")) {
                            Entity entity = serverPlayer.world.getEntityById(tag.getInt("Entity"));
                            if (entity != null) {
                                entity.setPos(message.pos().getX() + 0.5, message.pos().getY(), message.pos().getZ() + 0.5);
                            }
                        }
                        stack.setNbt(null);
                    }
                }
            };
        }
    }
}
