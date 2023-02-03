package tech.thatgravyboat.persona.common.network.messages.server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
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
    public static final ResourceLocation ID = new ResourceLocation(Personas.MOD_ID, "summon_persona");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public IPacketHandler<SummonPersonaMessage> getHandler() {
        return HANDLER;
    }

    private static class Handler implements IPacketHandler<SummonPersonaMessage> {

        @Override
        public void encode(SummonPersonaMessage message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.id());
            buffer.writeBlockPos(message.pos());
        }

        @Override
        public SummonPersonaMessage decode(FriendlyByteBuf buffer) {
            return new SummonPersonaMessage(buffer.readUtf(), buffer.readBlockPos());
        }

        @Override
        public Consumer<Player> handle(SummonPersonaMessage message) {
            return player -> {
                if (player instanceof ServerPlayer serverPlayer && serverPlayer.canUseGameMasterBlocks()) {
                    if (serverPlayer.server instanceof IPersonaHolder holder && holder.getPersonaManager() != null) {
                        NpcData data = holder.getPersonaManager().getNpc(message.id());
                        if (data == null) {
                            ((ServerPlayer) player).sendSystemMessage(Component.literal("Could not find persona."), true);
                            return;
                        }
                        Persona persona = Registry.PERSONA.get().create(serverPlayer.level);
                        if (persona != null) {
                            persona.setPersona(data);
                            persona.setPos(message.pos().getX() + 0.5, message.pos().getY(), message.pos().getZ() + 0.5);
                            serverPlayer.level.addFreshEntity(persona);
                        }
                    }
                }
            };
        }
    }
}

