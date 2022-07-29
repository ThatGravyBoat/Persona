package tech.thatgravyboat.persona.api.conditions.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.network.ServerPlayerEntity;
import tech.thatgravyboat.persona.api.CodecUtils;
import tech.thatgravyboat.persona.api.conditions.types.base.Condition;
import tech.thatgravyboat.persona.api.conditions.types.base.ConditionSerializer;

public record ExperienceCondition(boolean level, NumberRange.IntRange timeRange) implements Condition<ExperienceCondition> {

    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public boolean valid(ServerPlayerEntity player) {
        return timeRange.test(level ? player.experienceLevel : player.totalExperience);
    }

    @Override
    public ConditionSerializer<ExperienceCondition> serializer() {
        return SERIALIZER;
    }

    static class Serializer implements ConditionSerializer<ExperienceCondition> {

        public static final Codec<ExperienceCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.fieldOf("level").forGetter(ExperienceCondition::level),
                CodecUtils.passthrough(NumberRange.IntRange::toJson, NumberRange.IntRange::fromJson).fieldOf("time").forGetter(ExperienceCondition::timeRange)
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
