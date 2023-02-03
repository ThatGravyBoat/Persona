package tech.thatgravyboat.persona.common.network.fabric;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import tech.thatgravyboat.persona.common.network.handlers.IPacket;
import tech.thatgravyboat.persona.common.network.handlers.IPacketHandler;

public class NetPacketHandlerImpl {
    public static <T extends IPacket<T>> void sendToAllLoaded(T packet, Level level, BlockPos pos) {
        ChunkAccess chunk = level.getChunkAt(pos);
        ((ServerChunkCache)level.getChunkSource()).chunkMap.getPlayers(chunk.getPos(), false).forEach(player -> {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            packet.getHandler().encode(packet, buf);
            ServerPlayNetworking.send(player, packet.getID(), buf);
        });
    }

    public static <T> void registerServerToClientPacket(ResourceLocation location, IPacketHandler<T> handler, Class<T> tClass) {
        if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT))
            clientOnlyRegister(location, handler);
    }

    @Environment(EnvType.CLIENT)
    private static <T> void clientOnlyRegister(ResourceLocation location, IPacketHandler<T> handler) {
        ClientPlayNetworking.registerGlobalReceiver(location, (client, handler1, buf, responseSender) -> {
            T decode = handler.decode(buf);
            client.execute(() -> handler.handle(decode).accept(client.player));
        });
    }

    public static <T> void registerClientToServerPacket(ResourceLocation location, IPacketHandler<T> handler, Class<T> tClass) {
        ServerPlayNetworking.registerGlobalReceiver(location, (server, player, handler1, buf, responseSender) -> {
            T decode = handler.decode(buf);
            server.execute(() -> handler.handle(decode).accept(player));
        });
    }

    public static <T extends IPacket<T>> void sendToPlayer(T packet, Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            packet.getHandler().encode(packet, buf);
            ServerPlayNetworking.send(serverPlayer, packet.getID(), buf);
        }
    }

    public static <T extends IPacket<T>> void sendToServer(T packet) {
        sendToServerClientOnly(packet);
    }

    @Environment(EnvType.CLIENT)
    private static <T extends IPacket<T>> void sendToServerClientOnly(T packet) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        packet.getHandler().encode(packet, buf);
        ClientPlayNetworking.send(packet.getID(), buf);
    }
}
