package tech.thatgravyboat.persona.common.network;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import tech.thatgravyboat.persona.common.network.handlers.IPacket;
import tech.thatgravyboat.persona.common.network.handlers.IPacketHandler;
import tech.thatgravyboat.persona.common.network.messages.client.IdStateMessage;
import tech.thatgravyboat.persona.common.network.messages.server.*;

public class NetPacketHandler {

    public static void init() {
        registerServerToClientPacket(IdStateMessage.ID, IdStateMessage.HANDLER, IdStateMessage.class);
        registerClientToServerPacket(CheckIdMessage.ID, CheckIdMessage.HANDLER, CheckIdMessage.class);
        registerClientToServerPacket(CreatePersonaMessage.ID, CreatePersonaMessage.HANDLER, CreatePersonaMessage.class);
        registerClientToServerPacket(SelectEntityMessage.ID, SelectEntityMessage.HANDLER, SelectEntityMessage.class);
        registerClientToServerPacket(TeleportEntityMessage.ID, TeleportEntityMessage.HANDLER, TeleportEntityMessage.class);
        registerClientToServerPacket(RemoveEntityMessage.ID, RemoveEntityMessage.HANDLER, RemoveEntityMessage.class);
        registerClientToServerPacket(SummonPersonaMessage.ID, SummonPersonaMessage.HANDLER, SummonPersonaMessage.class);
        registerClientToServerPacket(PersonaFaceMessage.ID, PersonaFaceMessage.HANDLER, PersonaFaceMessage.class);
    }

    @ExpectPlatform
    public static <T extends IPacket<T>> void sendToAllLoaded(T packet, Level level, BlockPos pos) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends IPacket<T>> void sendToPlayer(T packet, Player player) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends IPacket<T>> void sendToServer(T packet) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T> void registerServerToClientPacket(ResourceLocation location, IPacketHandler<T> handler, Class<T> tClass) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T> void registerClientToServerPacket(ResourceLocation location, IPacketHandler<T> handler, Class<T> tClass) {
        throw new AssertionError();
    }

}
