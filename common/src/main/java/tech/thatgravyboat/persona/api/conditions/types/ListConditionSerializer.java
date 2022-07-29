package tech.thatgravyboat.persona.api.conditions.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import tech.thatgravyboat.persona.api.conditions.Conditions;
import tech.thatgravyboat.persona.api.conditions.types.base.Condition;
import tech.thatgravyboat.persona.api.conditions.types.base.ConditionSerializer;

import java.util.List;
import java.util.function.Function;

public class ListConditionSerializer<T extends Condition<T>> implements ConditionSerializer<T> {

    private final String id;
    public final Codec<T> codec;

    public ListConditionSerializer(String id, Function<List<Condition<?>>, T> condition, Function<T, List<Condition<?>>> list) {
        this.id = id;
        this.codec = RecordCodecBuilder.create(instance -> instance.group(
            Conditions.CODEC.listOf().fieldOf("conditions").forGetter(list)
        ).apply(instance, condition));
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public Codec<T> codec() {
        return this.codec;
    }
}
