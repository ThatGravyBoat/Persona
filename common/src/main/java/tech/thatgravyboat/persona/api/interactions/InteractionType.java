package tech.thatgravyboat.persona.api.interactions;

public enum InteractionType {
    RANDOM(true),
    LIST(true),
    CHAT(true),
    NOTHING(true),
    TRADE(false),
    COMMAND(true),
    DELAYED(true),
    IF(true);

    public final boolean canBeInList;

    InteractionType(boolean canBeInList) {
        this.canBeInList = canBeInList;
    }
}
