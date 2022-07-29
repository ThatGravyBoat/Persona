package tech.thatgravyboat.persona.common.network.handlers;

import net.minecraft.util.Identifier;

public interface IPacket<T> {
    Identifier getID();
    IPacketHandler<T> getHandler();
}