package tech.thatgravyboat.persona.common.lib;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public enum State {
    VALID(new TranslatableText("personas.naming.valid"), 0xffff55),
    SERVER_VALID(new TranslatableText("personas.naming.valid"), 0xffff55),
    ALREADY_EXISTS(new TranslatableText("personas.naming.already_exists"), 0xff5555),
    INVALID(new TranslatableText("personas.naming.invalid"), 0xff5555);

    public final Text displayText;
    public final int color;

    State(Text displayText, int color) {
        this.displayText = displayText;
        this.color = color;
    }
}
