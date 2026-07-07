package network.tserver.tnexus.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import com.mojang.brigadier.Message;

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

			Map<String, String> translations = loadTranslations(
				plugin,
				resourcePath,
				messagePath
			);

			store.registerAll(locale, translations);
		}

		return new TNexusMessages(store);
	}

	private static Map<String, String> loadTranslations(
		TNexusPlugin plugin,
		String resourcePath,
		Path messagePath
	) {
		Map<String, String> translations = loadDefaultTranslations(plugin, resourcePath);

		validateTranslations(translations, resourcePath);

		if (Files.exists(messagePath)) {
			try (FileConfig config = FileConfig.of(messagePath)) {
				config.load();
				flattenTranslations(config, "tnexus", translations, messagePath);
			}
		}

		return Map.copyOf(translations);
	}

	private static Map<String, String> loadDefaultTranslations(
		TNexusPlugin plugin,
		String resourcePath
	) {
		InputStream resource = plugin.getResource(resourcePath);

		if (resource == null) {
			throw new IllegalArgumentException("Bundled translation resource was not found: " + resourcePath);
		}

		Map<String, String> translations = new HashMap<>();

		try (
			resource;
			InputStreamReader reader = new InputStreamReader(
				resource,
				StandardCharsets.UTF_8
			);
		) {
			UnmodifiableConfig config = new TomlParser().parse(reader);

			flattenTranslations(
				config,
				"tnexus",
				translations,
				Path.of(resourcePath)
			);
		} catch (IOException exception) {
			throw new IllegalStateException(
				"Failed to read bundled translation source: " + resourcePath,
				exception
			);
		}

		return translations;
	}

	private static void flattenTranslations(
		UnmodifiableConfig config,
		String prefix,
		Map<String, String> translations,
		Path source
	) {
		for (UnmodifiableConfig.Entry entry : config.entrySet()) {
			String key = prefix + "." + entry.getKey();
			Object value = entry.getRawValue();

			if (value instanceof UnmodifiableConfig category) {
				flattenTranslations(
					category,
					key,
					translations,
					source
				);
				continue;
			}

			if (!(value instanceof String message)) {
				throw new IllegalArgumentException("Translation value must be a string: " + key + " in " + source);
			}

			translations.put(key, message);
		}
	}

	private static void validateTranslations(
		Map<String, String> translations,
		String resourcePath
	) {
		for (MessageKey messageKey : MessageKey.values()) {
			if (!translations.containsKey(messageKey.key())) {
				throw new IllegalArgumentException("Missing translation key: " + messageKey.key() + " in " + resourcePath);
			}
		}
	}

	public boolean register() {
		return GlobalTranslator.translator().addSource(this.store);
	}

	public boolean unregister() {
		return GlobalTranslator.translator().removeSource(this.store);
	}
}
