package tech.thatgravyboat.persona.common.management;

import net.minecraft.server.MinecraftServer;

public interface IPersonaHolder {

    PersonaManager getPersonaManager();

    void setPersonaManager(PersonaManager manager);

    MinecraftServer getPersonasServer();

    default void createOwnPersonaManager() {
        setPersonaManager(new PersonaManager(getPersonasServer()));
    }
}
