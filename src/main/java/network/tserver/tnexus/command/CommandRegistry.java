package network.tserver.tnexus.command;

import java.util.ArrayList;
import java.util.List;

import io.papermc.paper.command.brigadier.Commands;

public final class CommandRegistry {
	private final List<TNexusCommand> commands = new ArrayList<>();

	public void add(TNexusCommand command) {
		commands.add(command);
	}

	public void registerAll(Commands registrar) {
		for (TNexusCommand command : commands) {
			registrar.register(
				command.build(),
				command.description(),
				command.aliases()
			);
		}
	}
}
