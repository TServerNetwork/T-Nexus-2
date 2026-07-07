package network.tserver.tnexus.message;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.FileConfig;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.translation.MiniMessageTranslationStore;
import net.kyori.adventure.translation.GlobalTranslator;
import network.tserver.tnexus.TNexusPlugin;

public final class TNexusMessages {
	private static final Key STORE_NAME = Key.key("tnexus", "messages");
	private static final Map<Locale, String> LOCALE_RESOURCES = Map.of(
		Locale.JAPAN, "ja_JP",
		Locale.US,    "en_US"
	);
	private final MiniMessageTranslationStore store;

	private TNexusMessages(MiniMessageTranslationStore store) {
		this.store = Objects.requireNonNull(store, "store");
	}

	public static TNexusMessages load(
		TNexusPlugin plugin,
		Locale fallbackLocale
	) {
		Objects.requireNonNull(plugin, "plugin");
		Objects.requireNonNull(fallbackLocale, "fallbackLocale");

		if (!LOCALE_RESOURCES.containsKey(fallbackLocale)) {
			throw new IllegalArgumentException(
				"Unsupported fallback locale: " + fallbackLocale
			);
		}

		MiniMessageTranslationStore store = MiniMessageTranslationStore.create(STORE_NAME);
		store.defaultLocale(fallbackLocale);

		for (Map.Entry<Locale, String> localeEntry : LOCALE_RESOURCES.entrySet()) {
			Locale locale = localeEntry.getKey();
			String localeName = localeEntry.getValue();
			String resourcePath = "messages/" + localeName + ".toml";

			Path messagePath = plugin.getDataFolder()
			                         .toPath()
									 .resolve(resourcePath);
			
			if (Files.notExists(messagePath)) {
				plugin.saveResource(resourcePath, false);
			}

			Map<String, String> translations = loadTranslations(messagePath);

			store.registerAll(locale, translations);
		}

		return new TNexusMessages(store);
	}

	private static Map<String, String> loadTranslations(Path messagePath) {
		Map<String, String> translations = new HashMap<>();

		try (FileConfig config = FileConfig.of(messagePath)) {
			config.load();

			for (UnmodifiableConfig.Entry entry : config.entrySet()) {
				Object rawValue = entry.getRawValue();

				if (!(rawValue instanceof String message)) {
					throw new IllegalArgumentException(
						"Translation value must be a string: " + entry.getKey() + " in " + messagePath
					);
				}

				translations.put(entry.getKey(), message);
			}

			if (translations.isEmpty()) {
				throw new IllegalArgumentException(
					"Translation file is empty: " + messagePath
				);
			}

			return Map.copyOf(translations);
		}
	}

	public boolean register() {
		return GlobalTranslator.translator().addSource(this.store);
	}

	public boolean unregister() {
		return GlobalTranslator.translator().removeSource(this.store);
	}
}
