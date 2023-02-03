package tech.thatgravyboat.persona.api.conditions.types;

import net.minecraft.server.level.ServerPlayer;
import tech.thatgravyboat.persona.api.conditions.types.base.Condition;
import tech.thatgravyboat.persona.api.conditions.types.base.ConditionSerializer;

import java.util.List;

public record AndCondition(List<Condition<?>> conditions) implements Condition<AndCondition> {

    public static final ListConditionSerializer<AndCondition> SERIALIZER = new ListConditionSerializer<>("and", AndCondition::new, AndCondition::conditions);

    @Override
    public boolean valid(ServerPlayer player) {
        for (Condition<?> condition : conditions) if (!condition.valid(player)) return false;
        return true;
    }

    @Override
    public ConditionSerializer<AndCondition> serializer() {
        return SERIALIZER;
    }
}
