package open.openstats;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sipgate.mp3wav.Converter;
import io.socket.client.IO;
import io.socket.client.Socket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import javax.sound.sampled.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static open.openstats.openStats.LOGGER;

public class mbSocket {
    private Socket socket;

    private Clip audioClip;
    private long resumeTime = 0;

    public void loadAudio(String urlString) {
        try {
            LOGGER.info("[OpenStats] Fetching audio track.");
            // Fetching and converting
            URL url = new URL(urlString);
            InputStream audioInput = url.openStream();
            ByteArrayOutputStream wavOutput = new ByteArrayOutputStream();
            Converter converter = Converter.convertFrom(audioInput);
            converter.to(wavOutput);
            audioInput.close();
            InputStream audioInputStream = new ByteArrayInputStream(wavOutput.toByteArray());

            // Play
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioInputStream);
            audioInputStream.close();

            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);
            audioClip = (Clip) AudioSystem.getLine(info);

            // this will only play it once, no loop, but hopefully that's fine :P
            audioClip.open(audioStream);
            audioStream.close();
        } catch (Exception e) {
            LOGGER.error("Failed to load audio for MB! " + e);
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("§cCould not fetch the audio track for this round."));
        }
    }

    public void pause() {
        if (audioClip != null) {
            LOGGER.info("[OpenStats] MB music paused.");
            resumeTime = audioClip.getMicrosecondPosition();
            audioClip.stop();
        }
    }

    public void resume() {
        if (audioClip != null) {
            LOGGER.info("[OpenStats] MB music resumed.");
            audioClip.setMicrosecondPosition(resumeTime);
            audioClip.start();
        }
    }

    public void end() {
        if (audioClip != null) {
            LOGGER.info("[OpenStats] MB music ended.");
            audioClip.stop();
            audioClip.close();
        }
        audioClip = null;
    }

    public void setupSocket(String playerName) {
        IO.Options options;

        Map<String, String> authMap = new HashMap<>();
        authMap.put("name", playerName);

        options = new IO.Options();
        options.auth = authMap;
        options.reconnection = true;

        try {
            socket = IO.socket("https://mbn.k55.se", options);
        } catch (URISyntaxException e) {
            LOGGER.error("Could not create MB socket. Autoplay in Musical Blocks will not work, please restart your game to fix the problem.");
            MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of("§cCould not create MB socket. Autoplay in Musical Blocks will not work, please restart your game to fix the problem."));
            return;
        }

        socket.on(Socket.EVENT_CONNECT, args -> System.out.println("Connected to SOCKET server"));

        socket.on("song", args -> {
            JsonElement arg = JsonParser.parseString(Arrays.toString(args));
            String musicTrack = arg.getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString();
            resumeTime = 0; // important, wasted an hour on this :(
            loadAudio("https://antivpn.k55.se/tracks/" + musicTrack);
        });

        socket.on("play", args -> {
            resume();
        });

        socket.on("end", args -> {
            end();
        });

        socket.on("pause", args -> {
            pause();
        });
        socket.connect();
    }

    public void closeSocket() {
        LOGGER.info("Closing socket...");
        socket.disconnect();
    }
}
