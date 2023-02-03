package tech.thatgravyboat.persona.common.network.messages.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.client.screens.NamingScreen;
import tech.thatgravyboat.persona.common.lib.State;
import tech.thatgravyboat.persona.common.network.handlers.IPacket;
import tech.thatgravyboat.persona.common.network.handlers.IPacketHandler;

import java.util.function.Consumer;

public record IdStateMessage(String id, State state) implements IPacket<IdStateMessage> {

    public static final Handler HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(Personas.MOD_ID, "id_state");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public IPacketHandler<IdStateMessage> getHandler() {
        return HANDLER;
    }

    private static class Handler implements IPacketHandler<IdStateMessage> {

        @Override
        public void encode(IdStateMessage message, FriendlyByteBuf buffer) {
            buffer.writeUtf(message.id());
            buffer.writeEnum(message.state());
        }

        @Override
        public IdStateMessage decode(FriendlyByteBuf buffer) {
            return new IdStateMessage(buffer.readUtf(), buffer.readEnum(State.class));
        }

        @Override
        public Consumer<Player> handle(IdStateMessage message) {
            return player -> runClientTask(message);
        }

        @Environment(EnvType.CLIENT)
        private static void runClientTask(IdStateMessage message) {
            if (Minecraft.getInstance().screen instanceof NamingScreen namingScreen) {
                namingScreen.setStateForId(message.state(), message.id());
            }
        }
    }
}
