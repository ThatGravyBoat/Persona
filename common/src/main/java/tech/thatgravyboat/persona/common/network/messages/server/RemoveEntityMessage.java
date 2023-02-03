package tech.thatgravyboat.persona.common.network.messages.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.common.entity.Persona;
import tech.thatgravyboat.persona.common.network.handlers.IPacket;
import tech.thatgravyboat.persona.common.network.handlers.IPacketHandler;

import java.util.function.Consumer;

public record RemoveEntityMessage(int mob) implements IPacket<RemoveEntityMessage> {

    public static final Handler HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(Personas.MOD_ID, "remove_entity");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public IPacketHandler<RemoveEntityMessage> getHandler() {
        return HANDLER;
    }

    private static class Handler implements IPacketHandler<RemoveEntityMessage> {

        @Override
        public void encode(RemoveEntityMessage message, FriendlyByteBuf buffer) {
            buffer.writeVarInt(message.mob);
        }

        @Override
        public RemoveEntityMessage decode(FriendlyByteBuf buffer) {
            return new RemoveEntityMessage(buffer.readVarInt());
        }

        @Override
        public Consumer<Player> handle(RemoveEntityMessage message) {
            return player -> {
                if (player instanceof ServerPlayer serverPlayer && serverPlayer.canUseGameMasterBlocks()) {
                    Entity npc = serverPlayer.level.getEntity(message.mob());
                    if (npc instanceof Persona) {
                        npc.discard();
                    }
                }
            };
        }
    }
}
