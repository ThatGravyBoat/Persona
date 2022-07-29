package tech.thatgravyboat.persona.client.screens.appearance;

import com.google.gson.*;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Pair;
import tech.thatgravyboat.persona.Personas;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;

public class SkinHelper {

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final Gson GSON = new GsonBuilder().create();

    public static Pair<String, Boolean> getSkin(String username) {
        try {
            HttpRequest uuidRequest = createGetRequest("https://api.mojang.com/users/profiles/minecraft/"+username);

            HttpResponse<String> uuidResponse = CLIENT.send(uuidRequest, HttpResponse.BodyHandlers.ofString());
            if (uuidResponse.statusCode() == 200) {
                String uuid = JsonHelper.getString(GSON.fromJson(uuidResponse.body(), JsonObject.class), "id", null);
                if (uuid != null) {
                    HttpRequest skinRequest = createGetRequest("https://sessionserver.mojang.com/session/minecraft/profile/"+uuid);
                    HttpResponse<String> skinResponse = CLIENT.send(skinRequest, HttpResponse.BodyHandlers.ofString());
                    if (skinResponse.statusCode() == 200) {
                        return getBase64Textures(GSON.fromJson(skinResponse.body(), JsonObject.class))
                                .map(base64 -> getSkinUrl(GSON.fromJson(base64, JsonObject.class))).orElse(null);
                    }
                }
            }
        } catch (Exception ignored) {
            //DO NOTHING
        }
        return null;
    }

    private static Optional<String> getBase64Textures(JsonObject object) {
        for (JsonElement prop : JsonHelper.getArray(object, "properties", new JsonArray())) {
            if (prop instanceof JsonObject json) {
                if ("textures".equals(JsonHelper.getString(json, "name", null))) {
                    String value = JsonHelper.getString(json, "value", null);
                    if (value != null) {
                        return Optional.of(new String(Base64.getDecoder().decode(value)));
                    }
                }
            }
        }
        return Optional.empty();
    }

    private static Pair<String, Boolean> getSkinUrl(JsonObject object) {
        JsonObject textures = JsonHelper.getObject(object, "textures", new JsonObject());
        JsonObject skin = JsonHelper.getObject(textures, "SKIN", new JsonObject());
        String skinUrl = JsonHelper.getString(skin, "url", null);
        boolean slim = "slim".equals(JsonHelper.getString(JsonHelper.getObject(skin, "metadata", new JsonObject()), "model", null));
        return skinUrl == null ? null : new Pair<>(skinUrl, slim);
    }

    private static HttpRequest createGetRequest(String url) throws URISyntaxException {
        return HttpRequest.newBuilder(new URI(url))
                .GET()
                .version(HttpClient.Version.HTTP_2)
                .header("User-Agent", "Minecraft Mod (" + Personas.MOD_ID + "/1.0.0)")
                .build();
    }
}
