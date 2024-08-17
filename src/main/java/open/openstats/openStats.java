package open.openstats;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.socket.client.SocketOptionBuilder;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import open.openstats.informationScreen.infoScreen;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONObject;

import java.net.URISyntaxException;

import java.util.*;
import java.util.stream.Collectors;


public class openStats implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("openstats");
	private mbSocket musicSocket;
	private Thread socketThread;
	private String playerName = "";

	private void createMusicSocket(String playerName) {
		LOGGER.info("Creating new MB socket.");
		if (musicSocket != null) {
			musicSocket.closeSocket();
		}
		if (socketThread != null) {
			socketThread.interrupt();
		}

		musicSocket = new mbSocket();
		socketThread = new Thread(() -> {
			musicSocket.setupSocket(MinecraftClient.getInstance().player.getName().getString());
		});

		socketThread.start();
	}

	@Override
	public void onInitialize() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			ClientPlayerEntity player = MinecraftClient.getInstance().player;
			if (player != null) {
				if (!playerName.equals(player.getName().getString())) {
					playerName = player.getName().getString();
					createMusicSocket(playerName);
				}
			}
		});

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			LiteralArgumentBuilder<FabricClientCommandSource> lookupCommand = ClientCommandManager.literal("lookup")
					.executes(createFeedbackExecutor("lookup"))
					.then(ClientCommandManager.argument("player", StringArgumentType.string())
						.suggests((context, builder) -> CommandSource.suggestMatching(getOnlinePlayerNames(), builder))
						.executes(context -> {
							String playerName = StringArgumentType.getString(context, "player");
							MinecraftClient client = MinecraftClient.getInstance();
							// When executing a command the current screen will automatically be closed (the chat hud), this delays the new screen to open, so it won't close instantly
							client.send(() -> {
								JsonElement info = new fetchInformation().fetchProfile(playerName);
								if (info != null) {
									MinecraftClient.getInstance().setScreen(new infoScreen(info));
								}
							});
							return 1;
						})
					);
			LiteralCommandNode<FabricClientCommandSource> regLookupCommand = dispatcher.register(lookupCommand);

			registerAlias(dispatcher, "searchAPI", regLookupCommand);
			registerAlias(dispatcher, "openStats:searchAPI", regLookupCommand);
			registerAlias(dispatcher, "openStats:lookup", regLookupCommand);
		});
	}

	private void registerAlias(CommandDispatcher<FabricClientCommandSource> dispatcher, String alias, LiteralCommandNode<FabricClientCommandSource> targetCommand) {
		dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal(alias)
				.executes(createFeedbackExecutor(alias))
				.redirect(targetCommand));
	}

	private Command<FabricClientCommandSource> createFeedbackExecutor(String alias) {
		return context -> {
			context.getSource().sendFeedback(Text.of(Text.translatable("openstats.no_player").getString() + " §7/" + alias + " <player>"));
			return 1;
		};
	}

	private static Collection<String> getOnlinePlayerNames() {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player.networkHandler != null) {
			return client.getNetworkHandler().getPlayerList().stream()
					.map(player -> player.getProfile().getName())
					.collect(Collectors.toList());
		}
		return Collections.emptyList();
	}
}