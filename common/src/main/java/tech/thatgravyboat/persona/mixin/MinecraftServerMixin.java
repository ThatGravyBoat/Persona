package tech.thatgravyboat.persona.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.persona.common.management.IPersonaHolder;
import tech.thatgravyboat.persona.common.management.PersonaManager;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin implements IPersonaHolder {

    private PersonaManager persona$manager;

    @Override
    public PersonaManager getPersonaManager() {
        return persona$manager;
    }

    @Override
    public void setPersonaManager(PersonaManager manager) {
        this.persona$manager = manager;
    }

    @Override
    public MinecraftServer getPersonasServer() {
        return ((MinecraftServer)(Object)this);
    }

    @Inject(method = "loadLevel", at = @At("HEAD"))
    private void onServerLoad(CallbackInfo ci) {
        createOwnPersonaManager();
    }
}
