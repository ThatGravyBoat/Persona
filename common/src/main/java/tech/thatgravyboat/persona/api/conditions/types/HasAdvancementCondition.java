package tech.thatgravyboat.persona.api.conditions.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.api.conditions.types.base.Condition;
import tech.thatgravyboat.persona.api.conditions.types.base.ConditionSerializer;

public record HasAdvancementCondition(ResourceLocation id) implements Condition<HasAdvancementCondition> {

    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public boolean valid(ServerPlayer player) {
        var advancement = player.server.getAdvancements().getAdvancement(id);
        if (advancement == null) {
            Personas.LOGGER.error("Could not find advancement {}", id);
        }
        return advancement != null && player.getAdvancements().getOrStartProgress(advancement).isDone();
    }

    @Override
    public ConditionSerializer<HasAdvancementCondition> serializer() {
        return SERIALIZER;
    }

    static class Serializer implements ConditionSerializer<HasAdvancementCondition> {

        public static final Codec<HasAdvancementCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("advancement").forGetter(HasAdvancementCondition::id)
        ).apply(instance, HasAdvancementCondition::new));


        @Override
        public String id() {
            return "advancement";
        }

        @Override
        public Codec<HasAdvancementCondition> codec() {
            return CODEC;
        }
    }
}
