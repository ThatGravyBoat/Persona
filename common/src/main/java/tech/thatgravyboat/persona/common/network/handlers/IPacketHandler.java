package tech.thatgravyboat.persona.common.network.handlers;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.function.Consumer;

public interface IPacketHandler<T> {

    void encode(T message, FriendlyByteBuf buffer);

    T decode(FriendlyByteBuf buffer);

    Consumer<Player> handle(T message);
}
