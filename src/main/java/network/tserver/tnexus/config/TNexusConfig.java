package network.tserver.tnexus.config;

import java.nio.file.Path;

import com.electronwill.nightconfig.core.file.FileConfig;

import network.tserver.tnexus.TNexusPlugin;

public record TNexusConfig(boolean debug) {
	public static TNexusConfig load(TNexusPlugin plugin) {
		plugin.saveResource("config.toml", false);

		Path configPath = plugin.getDataFolder()
		                        .toPath()
								.resolve("config.toml");
		
		try (FileConfig config = FileConfig.of(configPath)) {
			config.load();

			return new TNexusConfig(
				config.getOrElse("debug", false)
			);
		}
	}
}