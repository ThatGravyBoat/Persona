package tech.thatgravyboat.persona.common.utils.forge;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class ModUtilsImpl {
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static boolean isModLoaded(String mod) {
        return ModList.get().isLoaded(mod);
    }
}
