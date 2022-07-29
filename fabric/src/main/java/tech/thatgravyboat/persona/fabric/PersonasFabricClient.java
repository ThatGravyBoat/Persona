package tech.thatgravyboat.persona.fabric;

import net.fabricmc.api.ClientModInitializer;
import tech.thatgravyboat.persona.client.PersonasClient;

public class PersonasFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PersonasClient.init();
    }
}
