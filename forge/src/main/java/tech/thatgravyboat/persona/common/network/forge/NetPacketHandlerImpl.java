package tech.thatgravyboat.persona.common.network.forge;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.common.network.handlers.IPacket;
import tech.thatgravyboat.persona.common.network.handlers.IPacketHandler;

public class NetPacketHandlerImpl {

    private static int id = 0;
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new Identifier(Personas.MOD_ID, "main_channel"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static <T extends IPacket<T>> void sendToAllLoaded(T packet, World level, BlockPos pos) {
        INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getWorldChunk(pos)), packet);
    }

    public static <T extends IPacket<T>> void sendToPlayer(T packet, PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayer)
            INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), packet);
    }

    public static <T> void registerServerToClientPacket(Identifier location, IPacketHandler<T> handler, Class<T> tClass) {
        clientOnlyRegister(handler, tClass);
    }

    private static <T> void clientOnlyRegister(IPacketHandler<T> handler, Class<T> tClass) {
        INSTANCE.registerMessage(++id, tClass, handler::encode, handler::decode, (t, context) -> {
            PlayerEntity sender = context.get().getDirection().getReceptionSide().equals(LogicalSide.CLIENT) ? getPlayer() : null;
            if(sender != null) {
                context.get().enqueueWork(() -> handler.handle(t).accept(sender));
            }
            context.get().setPacketHandled(true);
        });
    }

    @OnlyIn(Dist.CLIENT)
    private static PlayerEntity getPlayer() {
        return MinecraftClient.getInstance().player;
    }

    public static <T> void registerClientToServerPacket(Identifier location, IPacketHandler<T> handler, Class<T> tClass) {
        INSTANCE.registerMessage(++id, tClass, handler::encode, handler::decode, (t, context) -> {
            PlayerEntity sender = context.get().getSender();
            if(sender != null) {
                context.get().enqueueWork(() -> handler.handle(t).accept(sender));
            }
            context.get().setPacketHandled(true);
        });
    }

    public static <T extends IPacket<T>> void sendToServer(T packet) {
        INSTANCE.sendToServer(packet);
    }
}
