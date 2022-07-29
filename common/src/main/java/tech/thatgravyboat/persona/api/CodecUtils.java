package tech.thatgravyboat.persona.api;

import com.google.gson.JsonElement;
import com.mojang.datafixers.types.Func;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.registry.Registry;

import java.util.Optional;
import java.util.function.Function;

public class CodecUtils {

    public static final Codec<ItemStack> ITEM_STACK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Registry.ITEM.getCodec().fieldOf("id").forGetter(ItemStack::getItem),
            Codec.INT.fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
            NbtCompound.CODEC.optionalFieldOf("tag").forGetter(stack -> Optional.ofNullable(stack.getNbt()))
    ).apply(instance, CodecUtils::createStack));

    private static ItemStack createStack(Item item, int count, Optional<NbtCompound> nbt) {
        ItemStack stack = new ItemStack(item, count);
        nbt.ifPresent(stack::setNbt);
        return stack;
    }

    //region Passthrough
    //Taken from Resourceful Bees with permission from myself. :)
    public static <T> Codec<T> passthrough(Function<T, JsonElement> encoder, Function<JsonElement, T> decoder) {
        return Codec.PASSTHROUGH.comapFlatMap(dynamic -> decoder(dynamic, decoder), item -> encoder(item, encoder));
    }

    private static <T> DataResult<T> decoder(Dynamic<?> dynamic, Function<JsonElement, T> decoder) {
        if (dynamic.getValue() instanceof JsonElement jsonElement) {
            return DataResult.success(decoder.apply(jsonElement));
        } else {
            return DataResult.error("value was some how not a JsonElement");
        }
    }

    private static <T> Dynamic<JsonElement> encoder(T input, Function<T, JsonElement> encoder) {
        return new Dynamic<>(JsonOps.INSTANCE, encoder.apply(input));
    }
    //endregion

    public static <T, S> Codec<T> convert(Codec<S> codec, Function<S, T> from, Function<T, S> to) {
        return codec.comapFlatMap(item -> DataResult.success(from.apply(item)), to);
    }

    public static final Codec<EntityDimensions> DIMENSIONS_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("width").forGetter(d -> d.width),
            Codec.FLOAT.fieldOf("height").forGetter(d -> d.height),
            Codec.BOOL.fieldOf("fixed").orElse(false).forGetter(d -> d.fixed)
    ).apply(instance, EntityDimensions::new));
}
