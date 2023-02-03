package tech.thatgravyboat.persona.api.conditions.types;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import tech.thatgravyboat.persona.api.CodecUtils;
import tech.thatgravyboat.persona.api.conditions.types.base.Condition;
import tech.thatgravyboat.persona.api.conditions.types.base.ConditionSerializer;

public record PlayerHasItemCondition(ItemPredicate predicate) implements Condition<PlayerHasItemCondition> {

    public static final Serializer SERIALIZER = new Serializer();

    @Override
    public boolean valid(ServerPlayer player) {
        Inventory inventory = player.getInventory();
        for (NonNullList<ItemStack> itemStacks : ImmutableList.of(inventory.items, inventory.armor, inventory.offhand)) {
            for (ItemStack itemStack : itemStacks) {
                if (predicate().matches(itemStack)) return true;
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
                CodecUtils.passthrough(ItemPredicate::serializeToJson, ItemPredicate::fromJson).fieldOf("predicate").forGetter(PlayerHasItemCondition::predicate)
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
