package network.tserver.tnexus;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import network.tserver.tnexus.command.CommandRegistry;
import network.tserver.tnexus.command.TNexusRootCommand;
import network.tserver.tnexus.runtime.TNexusRuntime;

public final class TNexusBootstrap implements PluginBootstrap {
	private final TNexusRuntime runtime = new TNexusRuntime();
	
	@Override
	public void bootstrap(BootstrapContext context) {
		CommandRegistry commandRegistry = new CommandRegistry(context.getLogger());

		commandRegistry.add(new TNexusRootCommand(this.runtime));

		context.getLifecycleManager().registerEventHandler(
			LifecycleEvents.COMMANDS,
			event -> commandRegistry.registerAll(event.registrar())
		);
	}

	@Override
	public TNexusPlugin createPlugin(PluginProviderContext context) {
		return new TNexusPlugin(this.runtime);
	}
}
