package tech.thatgravyboat.persona.common.network.messages.server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.api.NpcData;
import tech.thatgravyboat.persona.common.entity.Persona;
import tech.thatgravyboat.persona.common.lib.State;
import tech.thatgravyboat.persona.common.management.IPersonaHolder;
import tech.thatgravyboat.persona.common.network.NetPacketHandler;
import tech.thatgravyboat.persona.common.network.handlers.IPacket;
import tech.thatgravyboat.persona.common.network.handlers.IPacketHandler;
import tech.thatgravyboat.persona.common.network.messages.client.IdStateMessage;
import tech.thatgravyboat.persona.common.registry.Registry;

import java.util.function.Consumer;

public record CreatePersonaMessage(String data, BlockPos pos) implements IPacket<CreatePersonaMessage> {

    private static final Gson GSON = new Gson();

    public static final Handler HANDLER = new Handler();
    public static final Identifier ID = new Identifier(Personas.MOD_ID, "create_persona");

    public CreatePersonaMessage(NpcData data, BlockPos pos) {
        this(NpcData.CODEC.encodeStart(JsonOps.COMPRESSED, data).result().map(JsonElement::toString).orElse(null), pos);
    }

    @Nullable
    public NpcData getData() {
        return NpcData.CODEC.parse(JsonOps.COMPRESSED, GSON.fromJson(data(), JsonElement.class)).result().orElse(null);
    }

    @Override
    public Identifier getID() {
        return ID;
    }

    @Override
    public IPacketHandler<CreatePersonaMessage> getHandler() {
        return HANDLER;
    }

    private static class Handler implements IPacketHandler<CreatePersonaMessage> {

        @Override
        public void encode(CreatePersonaMessage message, PacketByteBuf buffer) {
            buffer.writeString(message.data());
            buffer.writeBlockPos(message.pos());
        }

        @Override
        public CreatePersonaMessage decode(PacketByteBuf buffer) {
            return new CreatePersonaMessage(buffer.readString(), buffer.readBlockPos());
        }

        @Override
        public Consumer<PlayerEntity> handle(CreatePersonaMessage message) {
            return player -> {
                if (player instanceof ServerPlayerEntity serverPlayer && serverPlayer.isCreativeLevelTwoOp()) {
                    if (serverPlayer.server instanceof IPersonaHolder holder && holder.getPersonaManager() != null) {
                        NpcData data = message.getData();
                        if (data == null) {
                            player.sendMessage(Text.of("Persona was null"), true);
                            return;
                        }
                        holder.getPersonaManager().addDirtyNpc(data.id(), data);
                        holder.getPersonaManager().saveAll();
                        Persona persona = Registry.PERSONA.get().create(serverPlayer.world);
                        if (persona != null) {
                            persona.setPersona(data);
                            persona.setPos(message.pos().getX() + 0.5, message.pos().getY(), message.pos().getZ() + 0.5);
                            serverPlayer.world.spawnEntity(persona);
                        }
                    }
                }
            };
        }
    }
}
