package network.tserver.tnexus.config;

import java.nio.file.Files;
import java.nio.file.Path;

import com.electronwill.nightconfig.core.file.FileConfig;

import network.tserver.tnexus.TNexusPlugin;

/**
 * {@code config.toml} から読み込まれる不変の設定値です。
 *
 * @param debug デバッグ向けの挙動を有効にするかどうか
 */
public record TNexusConfig(boolean debug) {
	/**
	 * ディスクから設定を読み込み、必要ならデフォルト設定ファイルを生成します。
	 *
	 * @param plugin 動作中のプラグインインスタンス
	 * @return 読み込まれた設定のスナップショット
	 */
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
