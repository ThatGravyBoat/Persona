package tech.thatgravyboat.persona.api.conditions.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.network.ServerPlayerEntity;
import tech.thatgravyboat.persona.api.CodecUtils;
import tech.thatgravyboat.persona.api.conditions.types.base.Condition;
import tech.thatgravyboat.persona.api.conditions.types.base.ConditionSerializer;

import java.time.Instant;
import java.time.LocalDateTime;

public record RealTimeCondition(NumberRange.IntRange timeRange) implements Condition<RealTimeCondition> {

    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public boolean valid(ServerPlayerEntity player) {
        return timeRange.test(LocalDateTime.now().toLocalTime().toSecondOfDay());
    }

    @Override
    public ConditionSerializer<RealTimeCondition> serializer() {
        return SERIALIZER;
    }

    static class Serializer implements ConditionSerializer<RealTimeCondition> {

        public static final Codec<RealTimeCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CodecUtils.passthrough(NumberRange.IntRange::toJson, NumberRange.IntRange::fromJson).fieldOf("time").forGetter(RealTimeCondition::timeRange)
        ).apply(instance, RealTimeCondition::new));

        @Override
        public String id() {
            return "realtime";
        }

        @Override
        public Codec<RealTimeCondition> codec() {
            return CODEC;
        }
    }
}
