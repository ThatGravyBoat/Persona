package tech.thatgravyboat.persona.common.network.messages.server;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.common.network.handlers.IPacket;
import tech.thatgravyboat.persona.common.network.handlers.IPacketHandler;

import java.util.function.Consumer;

public record PersonaFaceMessage(int mob) implements IPacket<PersonaFaceMessage> {

    public static final Handler HANDLER = new Handler();
    public static final ResourceLocation ID = new ResourceLocation(Personas.MOD_ID, "face_me");



    @Override
    public ResourceLocation getID() {
        return ID;
    }

    @Override
    public IPacketHandler<PersonaFaceMessage> getHandler() {
        return HANDLER;
    }

    private static class Handler implements IPacketHandler<PersonaFaceMessage> {

        @Override
        public void encode(PersonaFaceMessage message, FriendlyByteBuf buffer) {
            buffer.writeVarInt(message.mob);
        }

        @Override
        public PersonaFaceMessage decode(FriendlyByteBuf buffer) {
            return new PersonaFaceMessage(buffer.readVarInt());
        }

        @Override
        public Consumer<Player> handle(PersonaFaceMessage message) {
            return player -> {
                if (player instanceof ServerPlayer serverPlayer && serverPlayer.canUseGameMasterBlocks()) {
                    Entity npc = serverPlayer.level.getEntity(message.mob());
                    if (npc != null) {
                        npc.lookAt(EntityAnchorArgument.Anchor.EYES, player.position());
                    }
                }
            };
        }
    }
}
