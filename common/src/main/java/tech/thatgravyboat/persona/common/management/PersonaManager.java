package tech.thatgravyboat.persona.common.management;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.WorldSavePath;
import tech.thatgravyboat.persona.Personas;
import tech.thatgravyboat.persona.api.NpcData;
import tech.thatgravyboat.persona.common.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PersonaManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path dataPath;
    private final Map<String, NpcDataHolder> data = new HashMap<>();

    public PersonaManager(MinecraftServer server) {
        this.dataPath = server.getSavePath(WorldSavePath.ROOT).resolve("personas");
        updateData();
    }

    public void updateData() {
        File dataFile = this.dataPath.toFile();
        if (dataFile.exists() || dataFile.mkdirs()) {
            data.clear();
            FileUtils.streamFilesAndParse(this.dataPath, this::parsePersona, "Could not stream personas!");
        }
    }

    private void parsePersona(Reader reader, String id) {
        JsonObject json = JsonHelper.deserialize(GSON, reader, JsonObject.class);
        addNpc(id, NpcData.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false, e -> {
            Personas.LOGGER.error("Could not parse persona with id '{}'", id);
            Personas.LOGGER.error(e);
        }));
    }

    public void addNpc(String id, NpcData data) {
        this.data.put(id, NpcDataHolder.of(data));
    }

    public void addDirtyNpc(String id, NpcData data) {
        this.data.put(id, NpcDataHolder.dirty(data));
    }

    public NpcData getNpc(String id) {
        NpcDataHolder holder = this.data.get(id);
        if (holder == null) return null;
        return holder.getData();
    }

    public boolean deleteNpc(String id) {
        this.data.remove(id);
        try {
            return new File(String.valueOf(this.dataPath), id+".json").delete();
        }catch (Exception e) {
            return false;
        }
    }

    public Set<String> npcIds() {
        return this.data.keySet();
    }

    public boolean isAlreadyAnNpc(String id) {
        return this.data.containsKey(id);
    }

    public void saveAll() {
        for (Map.Entry<String, NpcDataHolder> entry : this.data.entrySet()) {
            if (!entry.getValue().isDirty()) continue;
            entry.getValue().setDirty(false);
            NpcData.CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue().getData()).result().ifPresent(value -> {
                try {
                    org.apache.commons.io.FileUtils.writeStringToFile(new File(this.dataPath.toString(), entry.getKey()+".json"), GSON.toJson(value), StandardCharsets.UTF_8);
                } catch (IOException e) {
                    Personas.LOGGER.error("Could not save persona with id '{}'", entry.getKey());
                }
            });
        }
    }
}
