package network.tserver.tnexus.config;

import java.nio.file.Files;
import java.nio.file.Path;

import com.electronwill.nightconfig.core.file.FileConfig;

import network.tserver.tnexus.TNexusPlugin;

public record TNexusConfig(boolean debug) {
	public static TNexusConfig load(TNexusPlugin plugin) {
		Path configPath = plugin.getDataFolder()
		                        .toPath()
								.resolve("config.toml");
		
		if (Files.notExists(configPath)) {
			plugin.saveResource("config.toml", false);
		}
		
		try (FileConfig config = FileConfig.of(configPath)) {
			config.load();

			return new TNexusConfig(
				config.getOrElse("debug", false)
			);
		}
	}
}