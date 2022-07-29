package tech.thatgravyboat.persona.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import tech.thatgravyboat.persona.api.appearance.Appearance;
import tech.thatgravyboat.persona.api.appearance.Appearances;
import tech.thatgravyboat.persona.api.interactions.Interaction;
import tech.thatgravyboat.persona.api.interactions.Interactions;

public record NpcData(
        String id,
        String displayName,
        Appearance<?> appearance,
        Interaction<?> interaction,
        Features features
) {

    public static final Codec<NpcData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(NpcData::id),
            Codec.STRING.fieldOf("displayName").forGetter(NpcData::displayName),
            Appearances.CODEC.fieldOf("appearance").forGetter(NpcData::appearance),
            Interactions.CODEC.fieldOf("interactions").forGetter(NpcData::interaction),
            Features.CODEC.fieldOf("features").forGetter(NpcData::features)
    ).apply(instance, NpcData::new));
}
