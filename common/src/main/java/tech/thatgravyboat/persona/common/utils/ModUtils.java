package tech.thatgravyboat.persona.common.utils;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.nio.file.Path;

public class ModUtils {
    @ExpectPlatform
    public static Path getConfigDirectory() {
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isModLoaded(String mod) {
        throw new AssertionError();
    }
}
