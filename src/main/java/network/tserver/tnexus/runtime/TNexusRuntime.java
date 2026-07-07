package network.tserver.tnexus.runtime;

import java.util.Objects;

import network.tserver.tnexus.TNexusPlugin;
import network.tserver.tnexus.config.TNexusConfig;
import network.tserver.tnexus.message.TNexusMessages;

/**
 * ブートストラップ、プラグインライフサイクル、コマンドで共有する実行時状態を保持します。
 */
public final class TNexusRuntime {
	private TNexusPlugin plugin;
	private TNexusConfig config;
	private TNexusMessages messages;

	/**
	 * 有効化中のプラグインを保持し、設定を読み込んでランタイムを開始します。
	 *
	 * @param plugin 有効化されるプラグイン
	 */
	public void start(TNexusPlugin plugin) {
		if (this.plugin != null) {
			throw new IllegalStateException("T-Nexus runtime is already running.");
		}

		TNexusPlugin checkedPlugin = Objects.requireNonNull(plugin, "plugin");
		TNexusConfig loadedConfig  = TNexusConfig.load(checkedPlugin);
		TNexusMessages loadedMessages = TNexusMessages.load(
			checkedPlugin,
			loadedConfig.fallbackLocale()
		);

		if (!loadedMessages.register()) {
			throw new IllegalStateException("Failed to register T-Nexus translation source.");
		}

		this.plugin = checkedPlugin;
		this.config = loadedConfig;
		this.messages = loadedMessages;
	}

	/**
	 * プラグイン停止時にランタイム状態をクリアします。
	 */
	public void stop() {
		if (this.messages != null && !this.messages.unregister()) {
			this.plugin()
				.getSLF4JLogger()
				.warn("T-Nexus translation source was not registered.");
		}

		this.plugin = null;
		this.config = null;
		this.messages = null;
	}

	/**
	 * ランタイムが開始済みかどうかを返します。
	 *
	 * @return プラグインインスタンスが紐づいていれば {@code true}
	 */
	public boolean isRunning() {
		return this.plugin != null;
	}

	/**
	 * 現在動作中のプラグインインスタンスを返します。
	 *
	 * @return 動作中のプラグインインスタンス
	 */
	public TNexusPlugin plugin() {
		if (this.plugin == null) {
			throw new IllegalStateException("T-Nexus runtime is not running.");
		}

		return this.plugin;
	}

	/**
	 * プラグインメタデータに定義されたバージョン文字列を返します。
	 *
	 * @return 現在のプラグインバージョン
	 */
	public String version() {
		return this.plugin().getPluginMeta().getVersion();
	}

	/**
	 * 現在読み込まれている設定を返します。
	 *
	 * @return 有効な設定スナップショット
	 */
	public TNexusConfig config() {
		if (this.config == null) {
			throw new IllegalStateException("T-Nexus configuration is not loaded.");
		}

		return this.config;
	}

	/**
	 * ランタイムを維持したまま設定をディスクから再読み込みします。
	 */
	public void reload() {
		TNexusPlugin plugin = this.plugin();

		TNexusConfig loadedConfig = TNexusConfig.load(plugin);
		TNexusMessages loadedMessages = TNexusMessages.load(
			plugin,
			loadedConfig.fallbackLocale()
		);

		TNexusMessages currentMessages = this.messages();

		if (!currentMessages.unregister()) {
			throw new IllegalStateException("Failed to unregister the current T-Nexus translation source.");
		}

		try {
			if (!loadedMessages.register()) {
				throw new IllegalStateException("Failed to register the new T-Nexus translation source.");
			}
		} catch (RuntimeException exception) {
			if (!currentMessages.register()) {
				plugin.getSLF4JLogger().error("Failed to restore the previous T-Nexus translation source.");
			}


			throw exception;
		}

		this.config = loadedConfig;
		this.messages = loadedMessages;
	}

	public TNexusMessages messages() {
		if (this.messages == null) {
			throw new IllegalStateException("T-Nexus messages are not loaded.");
		}

		return this.messages;
	}
}
