package tech.thatgravyboat.persona.api.conditions.types.base;

import net.minecraft.server.level.ServerPlayer;
import tech.thatgravyboat.persona.common.entity.Persona;

public interface Condition<T extends Condition<T>> {

    default boolean valid(Persona persona, ServerPlayer player) {
        return valid(player);
    }

    default boolean valid(ServerPlayer player) {
        return false;
    }

    ConditionSerializer<T> serializer();
}
