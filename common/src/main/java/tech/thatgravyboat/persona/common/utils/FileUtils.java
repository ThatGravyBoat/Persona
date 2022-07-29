package tech.thatgravyboat.persona.common.utils;

import tech.thatgravyboat.persona.Personas;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * Modified FileUtils taken from Resourceful Bees.
 */
public class FileUtils {

    public static void streamFilesAndParse(Path directoryPath, BiConsumer<Reader, String> instructions, String errorMessage) {
        try (Stream<Path> jsonStream = Files.walk(directoryPath, 1)) {
            jsonStream.filter(f -> f.getFileName().toString().endsWith(".json")).forEach(path -> addFile(path, instructions));
        } catch (IOException e) {
            Personas.LOGGER.error(errorMessage, e);
        }
    }

    private static void addFile(Path path, BiConsumer<Reader, String> instructions) {
        File f = path.toFile();
        try {
            parseType(f, instructions);
        } catch (IOException e) {
            Personas.LOGGER.warn("File not found: {}", path);
        }
    }

    private static void parseType(File file, BiConsumer<Reader, String> consumer) throws IOException {
        String name = file.getName();
        name = name.substring(0, name.indexOf('.'));

        Reader r = Files.newBufferedReader(file.toPath());

        consumer.accept(r, name);
    }

}
