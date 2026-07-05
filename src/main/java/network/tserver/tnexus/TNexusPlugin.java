package network.tserver.tnexus;

import java.util.Objects;

import org.bukkit.plugin.java.JavaPlugin;

import network.tserver.tnexus.runtime.TNexusRuntime;

public final class TNexusPlugin extends JavaPlugin {
	private final TNexusRuntime runtime;

	public TNexusPlugin(TNexusRuntime runtime) {
		this.runtime = Objects.requireNonNull(runtime, "runtime");
	}

	@Override
	public void onEnable() {
		this.runtime.start(this);
		getSLF4JLogger().info("T-Nexus 2 enabled!");
	}

	@Override
	public void onDisable() {
		getSLF4JLogger().info("T-Nexus 2 disabled!");
		this.runtime.stop();
	}
}