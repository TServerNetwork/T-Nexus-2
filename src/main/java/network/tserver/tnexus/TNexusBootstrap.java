package network.tserver.tnexus;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import network.tserver.tnexus.command.CommandRegistry;
import network.tserver.tnexus.command.TNexusRootCommand;

public final class TNexusBootstrap implements PluginBootstrap {
	@Override
	public void bootstrap(BootstrapContext context) {
		CommandRegistry commandRegistry = new CommandRegistry(context.getLogger());

		commandRegistry.add(new TNexusRootCommand());

		context.getLifecycleManager().registerEventHandler(
			LifecycleEvents.COMMANDS,
			event -> commandRegistry.registerAll(event.registrar())
		);
	}
}
