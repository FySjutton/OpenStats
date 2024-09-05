package open.openutils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static open.openutils.OpenUtils.LOGGER;

public class FetchInformation {
    public JsonElement fetchProfile(String name) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://90gqopen.se/api/user/?username=" + name + "&event=true"))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            try {
                return JsonParser.parseString(response.body());
            } catch (Exception e) {
                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("§aOpenUtils §7- §c" + response.body()));
                LOGGER.error("Tried fetching information for \"" + name + "\" got \"" + response.body() + "\"");
            }
        } catch (Exception e) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(Text.translatable("openutils.error_encountered").getString() + e));
            LOGGER.error("Tried fetching information for \"" + name + "\" got \"" + e + "\"");
        }
        return null;
    }
}
