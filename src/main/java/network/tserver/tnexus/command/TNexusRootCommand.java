package network.tserver.tnexus.command;

import java.util.List;
import java.util.Objects;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import network.tserver.tnexus.runtime.TNexusRuntime;

public final class TNexusRootCommand implements TNexusCommand {
	private final TNexusRuntime runtime;

	public TNexusRootCommand(TNexusRuntime runtime) {
		this.runtime = Objects.requireNonNull(runtime, "runtime");
	}

	@Override
	public LiteralCommandNode<CommandSourceStack> build() {
		return Commands.literal("tnexus")
		               .executes(context -> {
							context.getSource()
							       .getSender()
								   .sendMessage(Component.text("T-Nexus " + this.runtime.version() + " is running."));
							return Command.SINGLE_SUCCESS;
					   })
					   .build();
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