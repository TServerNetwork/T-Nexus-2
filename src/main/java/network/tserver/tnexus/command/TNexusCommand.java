package network.tserver.tnexus.command;

import java.util.List;

import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;

public interface TNexusCommand {
	LiteralCommandNode<CommandSourceStack> build();
	String description();

	default List<String> aliases() {
		return List.of();
	}
}