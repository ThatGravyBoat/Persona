package tech.thatgravyboat.persona.api.interactions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.JsonHelper;
import tech.thatgravyboat.persona.api.interactions.types.*;
import tech.thatgravyboat.persona.api.interactions.types.base.InteractionSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Interactions {

    public static final Codec<InteractionSerializer<?>> TYPE_CODEC = Codec.STRING.comapFlatMap(Interactions::decode, InteractionSerializer::id);
    public static final Codec<Interaction<?>> CODEC = TYPE_CODEC.dispatch(Interaction::serializer, InteractionSerializer::codec);

    private static final Map<String, InteractionSerializer<?>> SERIALIZERS = new HashMap<>();

    static {
        add(ListInteraction.SERIALIZER);
        add(RandomInteraction.SERIALIZER);
        add(CommandInteraction.SERIALIZER);
        add(ChatInteraction.SERIALIZER);
        add(TradeInteraction.SERIALIZER);
        add(DelayedInteraction.SERIALIZER);
        add(IfInteraction.SERIALIZER);
        add(NothingInteraction.SERIALIZER);
    }

    private static DataResult<? extends InteractionSerializer<?>> decode(String id) {
        return Optional.ofNullable(SERIALIZERS.get(id)).map(DataResult::success).orElse(DataResult.error("No interaction type found."));
    }

    public static void add(InteractionSerializer<?> serializer) {
        SERIALIZERS.put(serializer.id(), serializer);
    }

}
