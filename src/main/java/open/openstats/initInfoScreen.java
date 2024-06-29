package open.openstats;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import open.openstats.informationScreen.infoScreen;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static open.openstats.openStats.LOGGER;

public class initInfoScreen {
    public void fetchProfile(String name) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://90gqopen.se/api/user/?username=" + name + "&event=true"))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            try {
                JsonElement data = JsonParser.parseString(response.body());
                MinecraftClient.getInstance().setScreen(new infoScreen(data));
            } catch (Exception e) {
                MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("§aOpenStats §7- §c" + response.body()));
                LOGGER.error("Tried fetching information for \"" + name + "\" got \"" + response.body() + "\"");
            }
        } catch (Exception e) {
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(Text.translatable("openstats.error_encountered").getString() + e));
            LOGGER.error("Tried fetching information for \"" + name + "\" got \"" + e + "\"");
        }
    }
}
