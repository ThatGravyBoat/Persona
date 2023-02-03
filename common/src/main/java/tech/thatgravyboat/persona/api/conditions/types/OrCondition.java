package tech.thatgravyboat.persona.api.conditions.types;

import net.minecraft.server.level.ServerPlayer;
import tech.thatgravyboat.persona.api.conditions.types.base.Condition;
import tech.thatgravyboat.persona.api.conditions.types.base.ConditionSerializer;

import java.util.List;

public record OrCondition(List<Condition<?>> conditions) implements Condition<OrCondition> {

    public static final ListConditionSerializer<OrCondition> SERIALIZER = new ListConditionSerializer<>("or", OrCondition::new, OrCondition::conditions);

    @Override
    public boolean valid(ServerPlayer player) {
        for (Condition<?> condition : conditions) if (condition.valid(player)) return true;
        return false;
    }

    @Override
    public ConditionSerializer<OrCondition> serializer() {
        return SERIALIZER;
    }
}
