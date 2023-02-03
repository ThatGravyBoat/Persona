package tech.thatgravyboat.persona.api.interactions.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.MerchantOffers;
import tech.thatgravyboat.persona.api.interactions.Interaction;
import tech.thatgravyboat.persona.api.interactions.InteractionType;
import tech.thatgravyboat.persona.api.interactions.types.base.InteractionSerializer;
import tech.thatgravyboat.persona.common.entity.Persona;
import tech.thatgravyboat.persona.common.utils.ItemTradeListing;

import java.util.List;
import java.util.OptionalInt;

public record TradeInteraction(List<ItemTradeListing> listings) implements Interaction<TradeInteraction> {

    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public InteractionType type() {
        return InteractionType.TRADE;
    }

    @Override
    public void activate(Persona persona, ServerPlayer player) {
        persona.setTradingPlayer(player);
        OptionalInt optionalInt = player.openMenu(new SimpleMenuProvider((id, inv, unused) ->
                new MerchantMenu(id, inv, persona), persona.getCustomName()));
        if (optionalInt.isPresent()) {
            MerchantOffers list = new MerchantOffers();
            listings()
                .stream()
                .map(ItemTradeListing::create)
                .forEachOrdered(list::add);
            if (!list.isEmpty()) {
                persona.overrideOffers(list);
                player.sendMerchantOffers(optionalInt.getAsInt(), list, 0, 0, false, false);
            }
        }

    }

    @Override
    public InteractionSerializer<TradeInteraction> serializer() {
        return SERIALIZER;
    }

    private static class Serializer implements InteractionSerializer<TradeInteraction> {

        public static final Codec<TradeInteraction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ItemTradeListing.CODEC.listOf().fieldOf("trades").forGetter(TradeInteraction::listings)
        ).apply(instance, TradeInteraction::new));

        @Override
        public String id() {
            return "trade";
        }

        @Override
        public Codec<TradeInteraction> codec() {
            return CODEC;
        }
    }
}
