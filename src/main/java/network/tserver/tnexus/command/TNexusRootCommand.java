package network.tserver.tnexus.command;

import java.util.List;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;

public final class TNexusRootCommand implements TNexusCommand {
	@Override
	public LiteralCommandNode<CommandSourceStack> build() {
		return Commands.literal("tnexus")
		               .executes(context -> {
							context.getSource()
							       .getSender()
								   .sendMessage(Component.text("T-Nexus 2 is running."));
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