package network.tserver.tnexus;

import org.bukkit.plugin.java.JavaPlugin;

public final class TNexusPlugin extends JavaPlugin {
	@Override
	public void onEnable() {
		getSLF4JLogger().info("T-Nexus 2 enabled!");
	}

	@Override
	public void onDisable() {
		getSLF4JLogger().info("T-Nexus 2 disabled!");
	}
}