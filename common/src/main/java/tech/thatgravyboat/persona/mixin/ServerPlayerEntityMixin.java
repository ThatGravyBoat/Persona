package tech.thatgravyboat.persona.mixin;

import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import tech.thatgravyboat.persona.common.lib.IPersonaCooldownHolder;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(ServerPlayer.class)
public class ServerPlayerEntityMixin implements IPersonaCooldownHolder {

    private final Map<String, Long> persona$personaOnCooldown = new ConcurrentHashMap<>();
    private final Map<UUID, Long> persona$entitiesOnCooldown = new ConcurrentHashMap<>();

    @Override
    public boolean isPersonaOnCooldown(String id, int time) {
        return persona$personaOnCooldown.getOrDefault(id, 0L) + time >= System.currentTimeMillis();
    }

    @Override
    public boolean isPersonaOnCooldown(UUID id, int time) {
        return persona$entitiesOnCooldown.getOrDefault(id, 0L) + time >= System.currentTimeMillis();
    }

    @Override
    public void setPersonaOnCooldown(String id) {
        persona$personaOnCooldown.put(id, System.currentTimeMillis());
    }

    @Override
    public void setPersonaOnCooldown(UUID id) {
        persona$entitiesOnCooldown.put(id, System.currentTimeMillis());
    }
}
