package tech.thatgravyboat.persona.common.network.messages.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.client.screens.NamingScreen;
import tech.thatgravyboat.persona.common.lib.State;
import tech.thatgravyboat.persona.common.network.handlers.IPacket;
import tech.thatgravyboat.persona.common.network.handlers.IPacketHandler;

import java.util.function.Consumer;

public record IdStateMessage(String id, State state) implements IPacket<IdStateMessage> {

    public static final Handler HANDLER = new Handler();
    public static final Identifier ID = new Identifier(Personas.MOD_ID, "id_state");

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public IPacketHandler<IdStateMessage> getHandler() {
        return HANDLER;
    }

    private static class Handler implements IPacketHandler<IdStateMessage> {

        @Override
        public void encode(IdStateMessage message, PacketByteBuf buffer) {
            buffer.writeString(message.id());
            buffer.writeEnumConstant(message.state());
        }

        @Override
        public IdStateMessage decode(PacketByteBuf buffer) {
            return new IdStateMessage(buffer.readString(), buffer.readEnumConstant(State.class));
        }

        @Override
        public Consumer<PlayerEntity> handle(IdStateMessage message) {
            return player -> runClientTask(message);
        }

        @Environment(EnvType.CLIENT)
        private static void runClientTask(IdStateMessage message) {
            if (MinecraftClient.getInstance().currentScreen instanceof NamingScreen namingScreen) {
                namingScreen.setStateForId(message.state(), message.id());
            }
        }
    }
}
