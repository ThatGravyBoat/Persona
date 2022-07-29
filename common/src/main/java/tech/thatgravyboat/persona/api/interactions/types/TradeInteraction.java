package tech.thatgravyboat.persona.api.interactions.types;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.village.TradeOfferList;
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
    public void activate(Persona persona, ServerPlayerEntity player) {
        persona.setCustomer(player);
        OptionalInt optionalInt = player.openHandledScreen(new SimpleNamedScreenHandlerFactory((id, inv, unused) ->
                new MerchantScreenHandler(id, inv, persona), persona.getCustomName()));
        if (optionalInt.isPresent()) {
            TradeOfferList list = new TradeOfferList();
            listings()
                .stream()
                .map(ItemTradeListing::create)
                .forEachOrdered(list::add);
            if (!list.isEmpty()) {
                persona.setOffersFromServer(list);
                player.sendTradeOffers(optionalInt.getAsInt(), list, 0, 0, false, false);
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
