package tech.thatgravyboat.persona.api;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.Function;

public class CodecUtils {

    public static final Codec<ItemStack> ITEM_STACK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Registry.ITEM.byNameCodec().fieldOf("id").forGetter(ItemStack::getItem),
            Codec.INT.fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
            CompoundTag.CODEC.optionalFieldOf("tag").forGetter(stack -> Optional.ofNullable(stack.getTag()))
    ).apply(instance, CodecUtils::createStack));

    private static ItemStack createStack(Item item, int count, Optional<CompoundTag> nbt) {
        ItemStack stack = new ItemStack(item, count);
        nbt.ifPresent(stack::setTag);
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
