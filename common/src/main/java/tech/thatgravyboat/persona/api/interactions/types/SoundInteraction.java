package tech.thatgravyboat.persona.api.interactions.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import tech.thatgravyboat.persona.api.interactions.Interaction;
import tech.thatgravyboat.persona.api.interactions.InteractionType;
import tech.thatgravyboat.persona.api.interactions.types.base.InteractionSerializer;
import tech.thatgravyboat.persona.common.entity.Persona;

public record SoundInteraction(SoundEvent event, float volume, float pitch) implements Interaction<SoundInteraction> {

    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public InteractionType type() {
        return InteractionType.SOUND;
    }

    @Override
    public void activate(Persona persona, ServerPlayer player) {
        player.playNotifySound(event, persona.getSoundSource(), volume, pitch);
    }

    @Override
    public InteractionSerializer<SoundInteraction> serializer() {
        return SERIALIZER;
    }

    private static class Serializer implements InteractionSerializer<SoundInteraction> {

        private static final Codec<SoundInteraction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Registry.SOUND_EVENT.byNameCodec().fieldOf("sound").forGetter(SoundInteraction::event),
                Codec.FLOAT.fieldOf("volume").orElse(1f).forGetter(SoundInteraction::volume),
                Codec.FLOAT.fieldOf("pitch").orElse(1f).forGetter(SoundInteraction::pitch)
        ).apply(instance, SoundInteraction::new));

        @Override
        public String id() {
            return "sound";
        }

        @Override
        public Codec<SoundInteraction> codec() {
            return CODEC;
        }
    }
}
