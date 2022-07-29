package tech.thatgravyboat.persona.api.conditions.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.network.ServerPlayerEntity;
import tech.thatgravyboat.persona.api.conditions.Conditions;
import tech.thatgravyboat.persona.api.conditions.types.base.Condition;
import tech.thatgravyboat.persona.api.conditions.types.base.ConditionSerializer;

public record NotCondition(Condition<?> condition) implements Condition<NotCondition> {

    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public boolean valid(ServerPlayerEntity player) {
        return !condition.valid(player);
    }

    @Override
    public ConditionSerializer<NotCondition> serializer() {
        return SERIALIZER;
    }

    static class Serializer implements ConditionSerializer<NotCondition> {

        public static final Codec<NotCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Conditions.CODEC.fieldOf("condition").forGetter(NotCondition::condition)
        ).apply(instance, NotCondition::new));

        @Override
        public String id() {
            return "not";
        }

        @Override
        public Codec<NotCondition> codec() {
            return CODEC;
        }
    }
}
