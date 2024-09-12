package open.openutils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static open.openutils.OpenUtils.LOGGER;

public class ConfigSystem {
    public static JsonElement configFile;

    public void checkConfig() {
        Path configDir = FabricLoader.getInstance().getConfigDir();

        if (Files.notExists(configDir.resolve("openutils.json5"))) {
            LOGGER.warn("OpenUtils: Configuration file not found - generating new config file.");
            try {
                InputStream resource = ConfigSystem.class.getResourceAsStream("/assets/openutils/default_config/openutils.json5");
                FileUtils.copyInputStreamToFile(resource, new File(configDir + "/openutils.json5"));
            } catch (Exception e) {
                LOGGER.error("OpenUtils - Could not generate a new openutils.json5 file (config), the program will now close. This error should not normally occur, and if you need help, please join our discord server. This error indicates that there's something wrong with the jar file, or the program doesn't have access to write files.");
                LOGGER.error("Shutting down minecraft..."); // Should just inactivate mod instead?
                e.printStackTrace();
                MinecraftClient.getInstance().stop();
            }
        }
        generateConfigArray();
    }

    private void generateConfigArray() {
        Path configDir = FabricLoader.getInstance().getConfigDir();

        try {
            File config = new File(configDir + "/openutils.json5");
            FileReader fileReader = new FileReader(config);
            JsonElement elm = JsonParser.parseReader(fileReader);
            fileReader.close();

            try {
                // VALIDATE THE "elm"
                JsonObject obj = elm.getAsJsonObject();
                obj.get("send_toast").getAsBoolean();
                obj.get("use_sound").getAsBoolean();
                obj.get("timer_change").getAsInt();
            } catch (Exception e) {
                LOGGER.error("OpenUtils: The configuration file does not appear to follow the required format. This might be caused by a missing key or similar. For help, join our discord server. You can try to delete the configuration file and than restart your game.");
                LOGGER.error("The error above is critical, and the game will automatically close now.");
                e.printStackTrace();
                MinecraftClient.getInstance().stop();
            }

            configFile = elm;
        } catch (Exception e) {
            LOGGER.error("OpenUtils - Could not load configuration file, this is most likely because of the file not following proper json syntax, like a missing comma or similar. For help, please seek help in Avox discord server.");
            LOGGER.error(e.getMessage());
            MinecraftClient.getInstance().stop();
        }
    }
}
