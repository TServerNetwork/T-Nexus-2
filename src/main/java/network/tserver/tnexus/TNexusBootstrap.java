package network.tserver.tnexus;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import network.tserver.tnexus.command.CommandRegistry;
import network.tserver.tnexus.command.TNexusRootCommand;
import network.tserver.tnexus.runtime.TNexusRuntime;

/**
 * プラグイン生成前に共有ランタイムとコマンド登録を初期化します。
 */
public final class TNexusBootstrap implements PluginBootstrap {
	private final TNexusRuntime runtime = new TNexusRuntime();
	
	/**
	 * Paper のブートストラップ段階でコマンド登録を準備します。
	 *
	 * @param context Paper のブートストラップコンテキスト
	 */
	@Override
	public void bootstrap(BootstrapContext context) {
		CommandRegistry commandRegistry = new CommandRegistry(context.getLogger());

		commandRegistry.add(new TNexusRootCommand(this.runtime));

		context.getLifecycleManager().registerEventHandler(
			LifecycleEvents.COMMANDS,
			event -> commandRegistry.registerAll(event.registrar())
		);
	}

	/**
	 * ブートストラップ済みランタイムを使うプラグインインスタンスを生成します。
	 *
	 * @param context プラグインプロバイダーのコンテキスト
	 * @return 共有ランタイムに紐づくプラグインインスタンス
	 */
	@Override
	public TNexusPlugin createPlugin(PluginProviderContext context) {
		return new TNexusPlugin(this.runtime);
	}
}
