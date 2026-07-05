package network.tserver.tnexus.runtime;

import java.util.Objects;

import network.tserver.tnexus.TNexusPlugin;
import network.tserver.tnexus.config.TNexusConfig;

/**
 * ブートストラップ、プラグインライフサイクル、コマンドで共有する実行時状態を保持します。
 */
public final class TNexusRuntime {
	private TNexusPlugin plugin;
	private TNexusConfig config;

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

		this.plugin = checkedPlugin;
		this.config = loadedConfig;
	}

	/**
	 * プラグイン停止時にランタイム状態をクリアします。
	 */
	public void stop() {
		this.plugin = null;
		this.config = null;
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
	public void reloadConfig() {
		TNexusConfig loadedConfig = TNexusConfig.load(this.plugin());

		this.config = loadedConfig;
	}
}
