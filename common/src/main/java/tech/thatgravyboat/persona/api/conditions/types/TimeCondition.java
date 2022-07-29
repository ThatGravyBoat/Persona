package tech.thatgravyboat.persona.api.conditions.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.network.ServerPlayerEntity;
import tech.thatgravyboat.persona.api.CodecUtils;
import tech.thatgravyboat.persona.api.conditions.types.base.Condition;
import tech.thatgravyboat.persona.api.conditions.types.base.ConditionSerializer;

public record TimeCondition(NumberRange.IntRange timeRange) implements Condition<TimeCondition> {

    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public boolean valid(ServerPlayerEntity player) {
        int time = (int) (player.world.getTimeOfDay() % 24000);
        return timeRange.test(time);
    }

    @Override
    public ConditionSerializer<TimeCondition> serializer() {
        return SERIALIZER;
    }

    static class Serializer implements ConditionSerializer<TimeCondition> {

        public static final Codec<TimeCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CodecUtils.passthrough(NumberRange.IntRange::toJson, NumberRange.IntRange::fromJson).fieldOf("time").forGetter(TimeCondition::timeRange)
        ).apply(instance, TimeCondition::new));

        @Override
        public String id() {
            return "time";
        }

        @Override
        public Codec<TimeCondition> codec() {
            return CODEC;
        }
    }
}
