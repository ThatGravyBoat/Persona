package tech.thatgravyboat.persona.common.network.fabric;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import tech.thatgravyboat.persona.common.network.handlers.IPacket;
import tech.thatgravyboat.persona.common.network.handlers.IPacketHandler;

public class NetPacketHandlerImpl {
    public static <T extends IPacket<T>> void sendToAllLoaded(T packet, World level, BlockPos pos) {
        Chunk chunk = level.getWorldChunk(pos);
        ((ServerChunkManager)level.getChunkManager()).threadedAnvilChunkStorage.getPlayersWatchingChunk(chunk.getPos(), false).forEach(player -> {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.getHandler().encode(packet, buf);
            ServerPlayNetworking.send(player, packet.getID(), buf);
        });
    }

    public static <T> void registerServerToClientPacket(Identifier location, IPacketHandler<T> handler, Class<T> tClass) {
        if (FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT))
            clientOnlyRegister(location, handler);
    }

    @Environment(EnvType.CLIENT)
    private static <T> void clientOnlyRegister(Identifier location, IPacketHandler<T> handler) {
        ClientPlayNetworking.registerGlobalReceiver(location, (client, handler1, buf, responseSender) -> {
            T decode = handler.decode(buf);
            client.execute(() -> handler.handle(decode).accept(client.player));
        });
    }

    public static <T> void registerClientToServerPacket(Identifier location, IPacketHandler<T> handler, Class<T> tClass) {
        ServerPlayNetworking.registerGlobalReceiver(location, (server, player, handler1, buf, responseSender) -> {
            T decode = handler.decode(buf);
            server.execute(() -> handler.handle(decode).accept(player));
        });
    }

    public static <T extends IPacket<T>> void sendToPlayer(T packet, PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            packet.getHandler().encode(packet, buf);
            ServerPlayNetworking.send(serverPlayer, packet.getID(), buf);
        }
    }

    public static <T extends IPacket<T>> void sendToServer(T packet) {
        sendToServerClientOnly(packet);
    }

    @Environment(EnvType.CLIENT)
    private static <T extends IPacket<T>> void sendToServerClientOnly(T packet) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        packet.getHandler().encode(packet, buf);
        ClientPlayNetworking.send(packet.getID(), buf);
    }
}
