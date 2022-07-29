package tech.thatgravyboat.persona.common.utils.fabric;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class ModUtilsImpl {

    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static boolean isModLoaded(String mod) {
        return FabricLoader.getInstance().isModLoaded(mod);
    }

}
