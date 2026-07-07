package network.tserver.tnexus.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.IllformedLocaleException;
import java.util.Locale;

import com.electronwill.nightconfig.core.file.FileConfig;

import network.tserver.tnexus.TNexusPlugin;

/**
 * {@code config.toml} から読み込まれる不変の設定値です。
 *
 * @param debug デバッグ向けの挙動を有効にするかどうか
 */
public record TNexusConfig(
	boolean debug,
	Locale fallbackLocale
) {
	private static final String DEFAULT_FALLBACK_LOCALE = "ja_JP";

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

			String fallbackLocale = config.getOrElse(
				"i18n.fallback-locale",
				DEFAULT_FALLBACK_LOCALE
			);

			return new TNexusConfig(
				config.getOrElse("debug", false),
				parseLocale(fallbackLocale)
			);
		}
	}

	private static Locale parseLocale(String value) {
		String languageTag = value.trim().replace('_', '-');

		try {
			return new Locale.Builder()
			                 .setLanguageTag(languageTag)
							 .build();
		} catch (IllformedLocaleException exception) {
			throw new IllegalArgumentException(
				"Invalid fallback locale: " + value,
				exception
			);
		}
	}
}
