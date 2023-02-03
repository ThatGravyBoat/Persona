package tech.thatgravyboat.persona.common.network.messages.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.common.lib.State;
import tech.thatgravyboat.persona.common.management.IPersonaHolder;
import tech.thatgravyboat.persona.common.network.NetPacketHandler;
import tech.thatgravyboat.persona.common.network.handlers.IPacket;
import tech.thatgravyboat.persona.common.network.handlers.IPacketHandler;
import tech.thatgravyboat.persona.common.network.messages.client.IdStateMessage;

import java.util.function.Consumer;
import java.util.regex.Pattern;

public record CheckIdMessage(String id) implements IPacket<CheckIdMessage> {

    public static final Handler HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(Personas.MOD_ID, "check_id");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public IPacketHandler<CheckIdMessage> getHandler() {
        return HANDLER;
    }

    private static class Handler implements IPacketHandler<CheckIdMessage> {

        private static final Pattern VALIDATOR = Pattern.compile("\\w+");

        @Override
        public void encode(CheckIdMessage message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.id());
        }

        @Override
        public CheckIdMessage decode(FriendlyByteBuf buffer) {
            return new CheckIdMessage(buffer.readUtf());
        }

        @Override
        public Consumer<Player> handle(CheckIdMessage message) {
            return player -> {
                if (player instanceof ServerPlayer serverPlayer && serverPlayer.canUseGameMasterBlocks()) {
                    if (serverPlayer.server instanceof IPersonaHolder holder && holder.getPersonaManager() != null) {
                        boolean alreadyAnNpc = holder.getPersonaManager().isAlreadyAnNpc(message.id());
                        State state = alreadyAnNpc ? State.ALREADY_EXISTS : message.id().length() > 2 && VALIDATOR.matcher(message.id()).matches() ? State.SERVER_VALID : State.INVALID;
                        NetPacketHandler.sendToPlayer(new IdStateMessage(message.id(), state), serverPlayer);
                    }
                }
            };
        }
    }
}
