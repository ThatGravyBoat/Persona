package tech.thatgravyboat.persona.api.conditions.types;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;
import tech.thatgravyboat.persona.api.CodecUtils;
import tech.thatgravyboat.persona.api.conditions.types.base.Condition;
import tech.thatgravyboat.persona.api.conditions.types.base.ConditionSerializer;

public record PlayerHasItemCondition(ItemPredicate predicate) implements Condition<PlayerHasItemCondition> {

    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public boolean valid(ServerPlayerEntity player) {
        PlayerInventory inventory = player.getInventory();
        for (DefaultedList<ItemStack> itemStacks : ImmutableList.of(inventory.main, inventory.armor, inventory.offHand)) {
            for (ItemStack itemStack : itemStacks) {
                if (predicate().test(itemStack)) return true;
            }
        }
        return false;
    }

    @Override
    public ConditionSerializer<PlayerHasItemCondition> serializer() {
        return SERIALIZER;
    }

    static class Serializer implements ConditionSerializer<PlayerHasItemCondition> {

        public static final Codec<PlayerHasItemCondition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CodecUtils.passthrough(ItemPredicate::toJson, ItemPredicate::fromJson).fieldOf("predicate").forGetter(PlayerHasItemCondition::predicate)
        ).apply(instance, PlayerHasItemCondition::new));

        @Override
        public String id() {
            return "has_item";
        }

        @Override
        public Codec<PlayerHasItemCondition> codec() {
            return CODEC;
        }
    }
}
