package network.tserver.tnexus.runtime;

import java.util.Objects;

import network.tserver.tnexus.TNexusPlugin;
import network.tserver.tnexus.config.TNexusConfig;

public final class TNexusRuntime {
	private TNexusPlugin plugin;
	private TNexusConfig config;

	public void start(TNexusPlugin plugin) {
		if (this.plugin != null) {
			throw new IllegalStateException("T-Nexus runtime is already running.");
		}

		TNexusPlugin checkedPlugin = Objects.requireNonNull(plugin, "plugin");
		TNexusConfig loadedConfig  = TNexusConfig.load(checkedPlugin);

		this.plugin = checkedPlugin;
		this.config = loadedConfig;
	}

	public void stop() {
		this.plugin = null;
		this.config = null;
	}

	public boolean isRunning() {
		return this.plugin != null;
	}

	public TNexusPlugin plugin() {
		if (this.plugin == null) {
			throw new IllegalStateException("T-Nexus runtime is not running.");
		}

		return this.plugin;
	}

	public String version() {
		return this.plugin().getPluginMeta().getVersion();
	}

	public TNexusConfig config() {
		if (this.config == null) {
			throw new IllegalStateException("T-Nexus configuration is not loaded.");
		}

		return this.config;
	}

	public void reloadConfig() {
		TNexusConfig loadedConfig = TNexusConfig.load(this.plugin());

		this.config = loadedConfig;
	}
}
