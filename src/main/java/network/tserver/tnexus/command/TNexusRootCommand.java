package network.tserver.tnexus.command;

import java.util.List;
import java.util.Objects;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import network.tserver.tnexus.runtime.TNexusRuntime;

public final class TNexusRootCommand implements TNexusCommand {
	private static final String RELOAD_PERMISSION = "tnexus.command.reload";
	private final TNexusRuntime runtime;

	public TNexusRootCommand(TNexusRuntime runtime) {
		this.runtime = Objects.requireNonNull(runtime, "runtime");
	}

	@Override
	public LiteralCommandNode<CommandSourceStack> build() {
		return Commands.literal("tnexus")
		               .executes(context ->
							this.executeInfo(context.getSource())
					   )
					   .then(
							Commands.literal("reload")
									.requires(source -> source.getSender().hasPermission(RELOAD_PERMISSION))
									.executes(context -> this.executeReload(context.getSource()))
					   ).build();
	}

	private int executeInfo(CommandSourceStack source) {
		source.getSender().sendMessage(
			Component.text("T-Nexus " + this.runtime.version() + " is running")
		);

		return Command.SINGLE_SUCCESS;
	}

	private int executeReload(CommandSourceStack source) {
		try {
			this.runtime.reloadConfig();
		} catch (RuntimeException exception) {
			this.runtime.plugin()
			            .getSLF4JLogger()
						.error(
							"Failed to reload T-Nexus configuration.",
							exception
						);
			source.getSender().sendMessage(
				Component.text(
					"Failed to reload T-Nexus configuration.",
					NamedTextColor.RED
				)
			);

			return 0;
		}

		source.getSender().sendMessage(
			Component.text(
				"T-Nexus configuration reloaded. Debug: " + this.runtime.config().debug(),
				NamedTextColor.GREEN
			)
		);

		return Command.SINGLE_SUCCESS;
	}

	@Override
	public String description() {
		return "Show information about T-Nexus.";
	}

	@Override
	public List<String> aliases() {
		return List.of("tn", "nexus");
	}
}