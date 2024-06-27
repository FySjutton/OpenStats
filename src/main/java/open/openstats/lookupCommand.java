package open.openstats;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class lookupCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registry) {
        LiteralArgumentBuilder<FabricClientCommandSource> command = ClientCommandManager.literal("lookup")
                .executes(context -> {
                    context.getSource().sendFeedback(Text.of("§cDu måste ange en spelare! §7/lookup <spelare>"));
                    return 1;
                })
                .then(ClientCommandManager.argument("player", StringArgumentType.string())
                        .suggests((context, builder) -> CommandSource.suggestMatching(getOnlinePlayerNames(), builder))
                        .executes(context -> {
                            String playerName = StringArgumentType.getString(context, "player");
                            MinecraftClient client = MinecraftClient.getInstance();
//                            When executing a command the current screen will automatically be closed (the chat hud), this delays the new screen to open, so it won't close instantly
                            client.send(() -> new initInfoScreen().fetchProfile(playerName));
                            return 1;
                        })
                );
        dispatcher.register(command);
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
