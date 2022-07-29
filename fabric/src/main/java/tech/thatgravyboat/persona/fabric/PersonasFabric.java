package tech.thatgravyboat.persona.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.common.management.PersonaCommand;

public class PersonasFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Personas.init();

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> PersonaCommand.register(dispatcher));
    }
}
