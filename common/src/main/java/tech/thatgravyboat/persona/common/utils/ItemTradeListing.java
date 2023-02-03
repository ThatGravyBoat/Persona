package tech.thatgravyboat.persona.common.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.jetbrains.annotations.NotNull;
import tech.thatgravyboat.persona.api.CodecUtils;

public record ItemTradeListing(
        ItemStack primaryItem,
        ItemStack secondaryItem,
        ItemStack givenItem,
        int maxUses,
        int rewardExp,
        float priceMultiplier
) implements VillagerTrades.ItemListing {

    public static final Codec<ItemTradeListing> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CodecUtils.ITEM_STACK_CODEC.fieldOf("primaryItem").forGetter(ItemTradeListing::primaryItem),
            CodecUtils.ITEM_STACK_CODEC.fieldOf("secondaryItem").orElse(ItemStack.EMPTY).forGetter(ItemTradeListing::secondaryItem),
            CodecUtils.ITEM_STACK_CODEC.fieldOf("givenItem").forGetter(ItemTradeListing::givenItem),
            Codec.INT.fieldOf("maxUses").orElse(1000).forGetter(ItemTradeListing::maxUses),
            Codec.INT.fieldOf("rewardExp").orElse(1).forGetter(ItemTradeListing::rewardExp),
            Codec.FLOAT.fieldOf("priceMultiplier").orElse(1f).forGetter(ItemTradeListing::priceMultiplier)
    ).apply(instance, ItemTradeListing::new));

    public MerchantOffer create() {
        return new MerchantOffer(this.primaryItem, this.secondaryItem, this.givenItem, this.maxUses, this.rewardExp, this.priceMultiplier);
    }

    @Override
    public @NotNull MerchantOffer getOffer(@NotNull Entity entity, @NotNull RandomSource random) {
        return create();
    }
}
