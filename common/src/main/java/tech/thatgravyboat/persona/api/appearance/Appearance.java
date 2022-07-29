package tech.thatgravyboat.persona.api.appearance;

public interface Appearance<T extends Appearance<T>> {
    AppearanceSerializer<T> serializer();
}
