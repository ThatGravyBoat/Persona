package tech.thatgravyboat.persona.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import org.jetbrains.annotations.Nullable;

public record Features(
        boolean shouldFacePlayer,
        boolean child,
        boolean sit,
        EntityDimensions dimensions,
        boolean nameTag
) {

    public static final Codec<Features> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("shouldFacePlayer").orElse(false).forGetter(Features::shouldFacePlayer),
            Codec.BOOL.fieldOf("child").orElse(false).forGetter(Features::child),
            Codec.BOOL.fieldOf("sit").orElse(false).forGetter(Features::sit),
            CodecUtils.DIMENSIONS_CODEC.fieldOf("dimensions").orElse(EntityDimensions.changing(1f, 2f)).forGetter(Features::dimensions),
            Codec.BOOL.fieldOf("nameTag").orElse(true).forGetter(Features::nameTag)
    ).apply(instance, Features::new));

    public static NbtCompound toNbt(@Nullable Features features) {
        return CODEC.encodeStart(NbtOps.INSTANCE, features).result()
                .filter(nbt -> nbt instanceof NbtCompound)
                .map(nbt -> (NbtCompound)nbt)
                .orElse(new NbtCompound());
    }

    public static Features fromNbt(NbtCompound nbt) {
        if (nbt.isEmpty()) return null;
        return CODEC.parse(NbtOps.INSTANCE, nbt).result().orElse(null);
    }
}
