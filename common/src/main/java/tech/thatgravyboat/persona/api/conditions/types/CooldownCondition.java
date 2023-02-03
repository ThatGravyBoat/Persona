package tech.thatgravyboat.persona.api.conditions.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import tech.thatgravyboat.persona.api.conditions.types.base.Condition;
import tech.thatgravyboat.persona.api.conditions.types.base.ConditionSerializer;
import tech.thatgravyboat.persona.common.entity.Persona;
import tech.thatgravyboat.persona.common.lib.IPersonaCooldownHolder;

public record CooldownCondition(boolean instance, int time) implements Condition<CooldownCondition> {

    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public boolean valid(Persona persona, ServerPlayer player) {
        if (player instanceof IPersonaCooldownHolder holder) {
            if (instance) {
                if (holder.isPersonaOnCooldown(persona.getUUID(), time)) {
                    return false;
                }
                holder.setPersonaOnCooldown(persona.getUUID());
            } else {
                if (holder.isPersonaOnCooldown(persona.npcId(), time)) {
                    return false;
                }
                holder.setPersonaOnCooldown(persona.npcId());
            }
            return true;
        }
        return false;
    }

    @Override
    public ConditionSerializer<CooldownCondition> serializer() {
        return SERIALIZER;
    }

    static class Serializer implements ConditionSerializer<CooldownCondition> {

        public static final Codec<CooldownCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.fieldOf("instance").forGetter(CooldownCondition::instance),
                Codec.INT.fieldOf("time").forGetter(CooldownCondition::time)
        ).apply(instance, CooldownCondition::new));

        @Override
        public String id() {
            return "cooldown";
        }

        @Override
        public Codec<CooldownCondition> codec() {
            return CODEC;
        }
    }
}
