package tech.thatgravyboat.persona.api.appearance;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import tech.thatgravyboat.persona.api.appearance.appearances.EntityAppearance;
import tech.thatgravyboat.persona.api.appearance.appearances.GeckoLibAppearance;
import tech.thatgravyboat.persona.api.appearance.appearances.NpcAppearance;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Appearances {

    private static final Map<String, AppearanceSerializer<?>> SERIALIZERS = new HashMap<>();

    public static final Codec<AppearanceSerializer<?>> TYPE_CODEC = Codec.STRING.comapFlatMap(Appearances::decode, AppearanceSerializer::id);
    public static final Codec<Appearance<?>> CODEC = TYPE_CODEC.dispatch(Appearance::serializer, AppearanceSerializer::codec);

    static {
        add(EntityAppearance.SERIALIZER);
        add(GeckoLibAppearance.SERIALIZER);
        add(NpcAppearance.SERIALIZER);
    }

    private static void add(AppearanceSerializer<?> serializer) {
        SERIALIZERS.put(serializer.id(), serializer);
    }

    private static DataResult<? extends AppearanceSerializer<?>> decode(String id) {
        return Optional.ofNullable(SERIALIZERS.get(id)).map(DataResult::success).orElse(DataResult.error("No appearabce type found."));
    }

    public static <T extends Appearance<T>> NbtCompound toNbt(Appearance<T> appearance) {
        return CODEC.encodeStart(NbtOps.INSTANCE, appearance).result()
                .filter(NbtCompound.class::isInstance)
                .map(NbtCompound.class::cast)
                .orElse(new NbtCompound());
    }

    public static Appearance<?> fromNbt(NbtCompound compound) {
        if (compound.isEmpty()) return null;
        return CODEC.parse(NbtOps.INSTANCE, compound).result().orElse(null);
    }
}
