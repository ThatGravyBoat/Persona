package tech.thatgravyboat.persona.api.conditions.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.server.level.ServerPlayer;
import tech.thatgravyboat.persona.api.CodecUtils;
import tech.thatgravyboat.persona.api.conditions.types.base.Condition;
import tech.thatgravyboat.persona.api.conditions.types.base.ConditionSerializer;

public record ExperienceCondition(boolean level, MinMaxBounds.Ints timeRange) implements Condition<ExperienceCondition> {

    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public boolean valid(ServerPlayer player) {
        return timeRange.matches(level ? player.experienceLevel : player.totalExperience);
    }

    @Override
    public ConditionSerializer<ExperienceCondition> serializer() {
        return SERIALIZER;
    }

    static class Serializer implements ConditionSerializer<ExperienceCondition> {

        public static final Codec<ExperienceCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.fieldOf("level").forGetter(ExperienceCondition::level),
                CodecUtils.passthrough(MinMaxBounds.Ints::serializeToJson, MinMaxBounds.Ints::fromJson).fieldOf("time").forGetter(ExperienceCondition::timeRange)
        ).apply(instance, ExperienceCondition::new));

        @Override
        public String id() {
            return "experience";
        }

        @Override
        public Codec<ExperienceCondition> codec() {
            return CODEC;
        }
    }
}
