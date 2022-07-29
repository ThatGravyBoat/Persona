package tech.thatgravyboat.persona.api.conditions.types.base;

import com.mojang.serialization.Codec;

public interface ConditionSerializer<T extends Condition<T>> {

    String id();

    Codec<T> codec();
}
