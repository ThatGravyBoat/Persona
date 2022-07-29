package tech.thatgravyboat.persona.common.network.handlers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

import java.util.function.Consumer;

public interface IPacketHandler<T> {

    void encode(T message, PacketByteBuf buffer);

    T decode(PacketByteBuf buffer);

    Consumer<PlayerEntity> handle(T message);
}
