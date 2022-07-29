package tech.thatgravyboat.persona.api.conditions.types.base;

import net.minecraft.server.network.ServerPlayerEntity;
import tech.thatgravyboat.persona.common.entity.Persona;

public interface Condition<T extends Condition<T>> {

    default boolean valid(Persona persona, ServerPlayerEntity player) {
        return valid(player);
    }

    default boolean valid(ServerPlayerEntity player) {
        return false;
    }

    ConditionSerializer<T> serializer();
}
