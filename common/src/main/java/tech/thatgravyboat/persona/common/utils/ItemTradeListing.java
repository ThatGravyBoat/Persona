package tech.thatgravyboat.persona.common.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.thatgravyboat.persona.api.CodecUtils;

import java.util.Random;

public record ItemTradeListing(
        ItemStack primaryItem,
        ItemStack secondaryItem,
        ItemStack givenItem,
        int maxUses,
        int rewardExp,
        float priceMultiplier
) implements TradeOffers.Factory {

    public static final Codec<ItemTradeListing> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CodecUtils.ITEM_STACK_CODEC.fieldOf("primaryItem").forGetter(ItemTradeListing::primaryItem),
            CodecUtils.ITEM_STACK_CODEC.fieldOf("secondaryItem").orElse(ItemStack.EMPTY).forGetter(ItemTradeListing::secondaryItem),
            CodecUtils.ITEM_STACK_CODEC.fieldOf("givenItem").forGetter(ItemTradeListing::givenItem),
            Codec.INT.fieldOf("maxUses").orElse(1000).forGetter(ItemTradeListing::maxUses),
            Codec.INT.fieldOf("rewardExp").orElse(1).forGetter(ItemTradeListing::rewardExp),
            Codec.FLOAT.fieldOf("priceMultiplier").orElse(1f).forGetter(ItemTradeListing::priceMultiplier)
    ).apply(instance, ItemTradeListing::new));

    public TradeOffer create() {
        return create(null, null);
    }

    @Override
    public @NotNull TradeOffer create(Entity entity, Random random) {
        return new TradeOffer(this.primaryItem, this.secondaryItem, this.givenItem, this.maxUses, this.rewardExp, this.priceMultiplier);
    }
}
