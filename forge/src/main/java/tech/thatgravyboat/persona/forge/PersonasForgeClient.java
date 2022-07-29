package tech.thatgravyboat.persona.forge;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import tech.thatgravyboat.persona.client.PersonasClient;

public class PersonasForgeClient {

    public static void clientSetup(FMLClientSetupEvent event) {
        PersonasClient.init();
    }
}
