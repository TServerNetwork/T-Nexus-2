package network.tserver.tnexus.runtime;

import java.util.Objects;

import network.tserver.tnexus.TNexusPlugin;

public final class TNexusRuntime {
	private TNexusPlugin plugin;
	public void start(TNexusPlugin plugin) {
		if (this.plugin != null) {
			throw new IllegalStateException("T-Nexus runtime is already running.");
		}

		this.plugin = Objects.requireNonNull(plugin, "plugin");
	}

	public void stop() {
		this.plugin = null;
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
}
