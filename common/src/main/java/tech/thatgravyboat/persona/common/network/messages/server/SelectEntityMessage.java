package tech.thatgravyboat.persona.common.network.messages.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.common.entity.PersonaSpawnEgg;
import tech.thatgravyboat.persona.common.network.handlers.IPacket;
import tech.thatgravyboat.persona.common.network.handlers.IPacketHandler;

import java.util.function.Consumer;

public record SelectEntityMessage(int mob) implements IPacket<SelectEntityMessage> {

    public static final Handler HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(Personas.MOD_ID, "select_entity");

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public IPacketHandler<SelectEntityMessage> getHandler() {
        return HANDLER;
    }

    private static class Handler implements IPacketHandler<SelectEntityMessage> {

        @Override
        public void encode(SelectEntityMessage message, FriendlyByteBuf buffer) {
            buffer.writeVarInt(message.mob);
        }

        @Override
        public SelectEntityMessage decode(FriendlyByteBuf buffer) {
            return new SelectEntityMessage(buffer.readVarInt());
        }

        @Override
        public Consumer<Player> handle(SelectEntityMessage message) {
            return player -> {
                if (player instanceof ServerPlayer serverPlayer && serverPlayer.canUseGameMasterBlocks()) {
                    Entity npc = serverPlayer.level.getEntity(message.mob());
                    ItemStack stack = player.getMainHandItem();
                    if (npc != null && stack.getItem() instanceof PersonaSpawnEgg) {
                        stack.getOrCreateTag().putInt("Entity", npc.getId());
                    }
                }
            };
        }
    }
}
