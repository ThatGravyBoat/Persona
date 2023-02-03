package tech.thatgravyboat.persona.common.network.messages.server;

import com.mojang.math.Vector3d;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.common.entity.PersonaSpawnEgg;
import tech.thatgravyboat.persona.common.network.handlers.IPacket;
import tech.thatgravyboat.persona.common.network.handlers.IPacketHandler;

import java.util.function.Consumer;

public record TeleportEntityMessage(Vector3d pos) implements IPacket<TeleportEntityMessage> {

    public static final Handler HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(Personas.MOD_ID, "teleport_entity");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public IPacketHandler<TeleportEntityMessage> getHandler() {
        return HANDLER;
    }

    private static class Handler implements IPacketHandler<TeleportEntityMessage> {

        @Override
        public void encode(TeleportEntityMessage message, FriendlyByteBuf buffer) {
            buffer.writeDouble(message.pos.x);
            buffer.writeDouble(message.pos.y);
            buffer.writeDouble(message.pos.z);
        }

        @Override
        public TeleportEntityMessage decode(FriendlyByteBuf buffer) {
            return new TeleportEntityMessage(new Vector3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
        }

        @Override
        public Consumer<Player> handle(TeleportEntityMessage message) {
            return player -> {
                if (player instanceof ServerPlayer serverPlayer && serverPlayer.canUseGameMasterBlocks()) {
                    ItemStack stack = player.getMainHandItem();
                    if (stack.getItem() instanceof PersonaSpawnEgg) {
                        CompoundTag tag = stack.getOrCreateTag();
                        if (tag.contains("Entity")) {
                            Entity entity = serverPlayer.level.getEntity(tag.getInt("Entity"));
                            if (entity != null) {
                                entity.setPos(message.pos().x + 0.5, message.pos().y, message.pos().z + 0.5);
                            }
                        }
                        stack.setTag(null);
                    }
                }
            };
        }
    }
}
