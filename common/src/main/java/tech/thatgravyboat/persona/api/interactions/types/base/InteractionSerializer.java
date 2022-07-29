package tech.thatgravyboat.persona.api.interactions.types.base;

import com.mojang.serialization.Codec;
import tech.thatgravyboat.persona.api.interactions.Interaction;

public interface InteractionSerializer<T extends Interaction<T>> {
    String id();
    Codec<T> codec();
}
