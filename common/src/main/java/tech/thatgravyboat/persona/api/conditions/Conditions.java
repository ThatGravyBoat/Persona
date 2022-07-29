package tech.thatgravyboat.persona.api.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import tech.thatgravyboat.persona.api.conditions.types.*;
import tech.thatgravyboat.persona.api.conditions.types.base.Condition;
import tech.thatgravyboat.persona.api.conditions.types.base.ConditionSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Conditions {


    public static final Codec<ConditionSerializer<?>> TYPE_CODEC = Codec.STRING.comapFlatMap(Conditions::decode, ConditionSerializer::id);
    public static final Codec<Condition<?>> CODEC = TYPE_CODEC.dispatch(Condition::serializer, ConditionSerializer::codec);

    private static final Map<String, ConditionSerializer<?>> SERIALIZERS = new HashMap<>();

    static {
        add(AndCondition.SERIALIZER);
        add(OrCondition.SERIALIZER);
        add(NotCondition.SERIALIZER);
        add(TimeCondition.SERIALIZER);
        add(RealTimeCondition.SERIALIZER);
        add(PlayerHasItemCondition.SERIALIZER);
        add(HasAdvancementCondition.SERIALIZER);
        add(ExperienceCondition.SERIALIZER);
        add(CooldownCondition.SERIALIZER);
    }

    public static void add(ConditionSerializer<?> serializer) {
        SERIALIZERS.put(serializer.id(), serializer);
    }

    private static DataResult<? extends ConditionSerializer<?>> decode(String id) {
        return Optional.ofNullable(SERIALIZERS.get(id)).map(DataResult::success).orElse(DataResult.error("No condition type found."));
    }

}
