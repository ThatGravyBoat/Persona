package tech.thatgravyboat.persona.api.conditions.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.server.level.ServerPlayer;
import tech.thatgravyboat.persona.api.CodecUtils;
import tech.thatgravyboat.persona.api.conditions.types.base.Condition;
import tech.thatgravyboat.persona.api.conditions.types.base.ConditionSerializer;

public record TimeCondition(MinMaxBounds.Ints timeRange) implements Condition<TimeCondition> {

    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public boolean valid(ServerPlayer player) {
        int time = (int) (player.level.getDayTime() % 24000);
        return timeRange.matches(time);
    }

    @Override
    public ConditionSerializer<TimeCondition> serializer() {
        return SERIALIZER;
    }

    static class Serializer implements ConditionSerializer<TimeCondition> {

        public static final Codec<TimeCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CodecUtils.passthrough(MinMaxBounds.Ints::serializeToJson, MinMaxBounds.Ints::fromJson).fieldOf("time").forGetter(TimeCondition::timeRange)
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
