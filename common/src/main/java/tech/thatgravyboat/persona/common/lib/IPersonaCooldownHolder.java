package tech.thatgravyboat.persona.common.lib;

import java.util.UUID;

public interface IPersonaCooldownHolder {

    boolean isPersonaOnCooldown(String id, int time);

    boolean isPersonaOnCooldown(UUID id, int time);

    void setPersonaOnCooldown(String id);

    void setPersonaOnCooldown(UUID id);
}
