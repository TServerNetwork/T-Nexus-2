package network.tserver.tnexus;

import java.util.Objects;

import org.bukkit.plugin.java.JavaPlugin;

import network.tserver.tnexus.runtime.TNexusRuntime;

/**
 * 共有ランタイムを利用する Paper プラグインのエントリーポイントです。
 */
public final class TNexusPlugin extends JavaPlugin {
	private final TNexusRuntime runtime;

	/**
	 * ブートストラップ段階で準備したランタイムを受け取って初期化します。
	 *
	 * @param runtime このプラグインインスタンスが利用する共有ランタイム
	 */
	public TNexusPlugin(TNexusRuntime runtime) {
		this.runtime = Objects.requireNonNull(runtime, "runtime");
	}

	/**
	 * ランタイムを開始し、起動ログを出力します。
	 */
	@Override
	public void onEnable() {
		this.runtime.start(this);
		getSLF4JLogger().info(
			"T-Nexus {} enabled! Debug: {}",
			this.runtime.version(),
			this.runtime.config().debug()
		);
	}

	/**
	 * ランタイムを停止し、終了ログを出力します。
	 */
	@Override
	public void onDisable() {
		getSLF4JLogger().info("T-Nexus 2 disabled!");
		this.runtime.stop();
	}
}
