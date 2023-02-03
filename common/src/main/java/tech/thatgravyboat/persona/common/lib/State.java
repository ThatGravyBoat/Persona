package tech.thatgravyboat.persona.common.lib;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum State {
    VALID(Component.translatable("personas.naming.valid"), 0xffff55),
    SERVER_VALID(Component.translatable("personas.naming.valid"), 0xffff55),
    ALREADY_EXISTS(Component.translatable("personas.naming.already_exists"), 0xff5555),
    INVALID(Component.translatable("personas.naming.invalid"), 0xff5555);

    public final MutableComponent displayText;
    public final int color;

    State(MutableComponent displayText, int color) {
        this.displayText = displayText;
        this.color = color;
    }
}
