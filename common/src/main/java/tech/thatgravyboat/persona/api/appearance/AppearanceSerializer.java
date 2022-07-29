package tech.thatgravyboat.persona.api.appearance;

import com.mojang.serialization.Codec;

public interface AppearanceSerializer<T extends Appearance<T>> {

    String id();

    Codec<T> codec();
}
