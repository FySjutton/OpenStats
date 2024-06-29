package open.openstats;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class openStats implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("openstats");

	@Override
	public void onInitialize() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			LiteralArgumentBuilder<FabricClientCommandSource> command = ClientCommandManager.literal("lookup")
					.executes(createFeedbackExecutor("lookup"))
					.then(ClientCommandManager.argument("player", StringArgumentType.string())
						.suggests((context, builder) -> CommandSource.suggestMatching(getOnlinePlayerNames(), builder))
						.executes(context -> {
							String playerName = StringArgumentType.getString(context, "player");
							MinecraftClient client = MinecraftClient.getInstance();
							// When executing a command the current screen will automatically be closed (the chat hud), this delays the new screen to open, so it won't close instantly
							client.send(() -> new initInfoScreen().fetchProfile(playerName));
							return 1;
						})
					);
			LiteralCommandNode<FabricClientCommandSource> registeredCommand = dispatcher.register(command);

			registerAlias(dispatcher, "searchAPI", registeredCommand);
			registerAlias(dispatcher, "openStats:searchAPI", registeredCommand);
			registerAlias(dispatcher, "openStats:lookup", registeredCommand);
		});
	}

	private void registerAlias(CommandDispatcher<FabricClientCommandSource> dispatcher, String alias, LiteralCommandNode<FabricClientCommandSource> targetCommand) {
		dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal(alias)
				.executes(createFeedbackExecutor(alias))
				.redirect(targetCommand));
	}

	private Command<FabricClientCommandSource> createFeedbackExecutor(String alias) {
		return context -> {
			context.getSource().sendFeedback(Text.of(Text.translatable("openstats.no_player") + " §7/" + alias + "<spelare>"));
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