package network.tserver.tnexus.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;

import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public final class CommandRegistry {
	private final List<TNexusCommand> commands = new ArrayList<>();
	private final Logger logger;

	public CommandRegistry(Logger logger) {
		this.logger = logger;
	}

	public void add(TNexusCommand command) {
		commands.add(command);
	}

	public void registerAll(Commands registrar) {
		for (TNexusCommand command : commands) {
			LiteralCommandNode<CommandSourceStack> node = command.build();
			Set<String> registeredLabels = registrar.register(
				node,
				command.description(),
				command.aliases()
			);

			String label = node.getLiteral();

			if (!registeredLabels.contains(label)) {
				this.logger.error("Failed to register command: /{}", label);
				continue;
			}

			for (String alias : command.aliases()) {
				if (!registeredLabels.contains(alias)) {
					this.logger.warn(
						"Alias /{} for /{} was not registered as a plain label; it may already be in use.",
						alias,
						label
					);
				}
			}
		}
	}
}
