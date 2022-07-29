package tech.thatgravyboat.persona.api.conditions.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.Advancement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.predicate.NumberRange;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.api.CodecUtils;
import tech.thatgravyboat.persona.api.conditions.types.base.Condition;
import tech.thatgravyboat.persona.api.conditions.types.base.ConditionSerializer;

public record HasAdvancementCondition(Identifier id) implements Condition<HasAdvancementCondition> {

    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public boolean valid(ServerPlayerEntity player) {
        var advancement = player.server.getAdvancementLoader().get(id);
        if (advancement == null) {
            Personas.LOGGER.error("Could not find advancement {}", id);
        }
        return advancement != null && player.getAdvancementTracker().getProgress(advancement).isDone();
    }

    @Override
    public ConditionSerializer<HasAdvancementCondition> serializer() {
        return SERIALIZER;
    }

    static class Serializer implements ConditionSerializer<HasAdvancementCondition> {

        public static final Codec<HasAdvancementCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("advancement").forGetter(HasAdvancementCondition::id)
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
