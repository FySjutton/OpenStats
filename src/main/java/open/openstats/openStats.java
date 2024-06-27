package open.openstats;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class openStats implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("openstats");

	@Override
	public void onInitialize() {
		ClientCommandRegistrationCallback.EVENT.register(lookupCommand::register);
	}
}