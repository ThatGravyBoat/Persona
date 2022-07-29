package tech.thatgravyboat.persona.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.common.management.PersonaCommand;
import tech.thatgravyboat.persona.common.registry.forge.RegistryImpl;

@Mod(Personas.MOD_ID)
public class PersonasForge {
    public PersonasForge() {
        Personas.init();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        RegistryImpl.ENTITY_TYPE.register(modEventBus);
        RegistryImpl.ITEM.register(modEventBus);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modEventBus.addListener(PersonasForgeClient::clientSetup));

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        PersonaCommand.register(event.getDispatcher());
    }


}
