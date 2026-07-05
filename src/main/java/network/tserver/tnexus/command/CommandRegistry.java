package network.tserver.tnexus.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;

import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

/**
 * プラグイン内のコマンドを収集し、Paper のコマンド登録機構へ渡します。
 */
public final class CommandRegistry {
	private final List<TNexusCommand> commands = new ArrayList<>();
	private final Logger logger;

	/**
	 * コマンド登録時の問題をログへ出力するレジストリを生成します。
	 *
	 * @param logger 登録診断に使うロガー
	 */
	public CommandRegistry(Logger logger) {
		this.logger = logger;
	}

	/**
	 * 後で登録するコマンド定義を追加します。
	 *
	 * @param command 保持するコマンド定義
	 */
	public void add(TNexusCommand command) {
		commands.add(command);
	}

	/**
	 * 保持している全コマンドを登録し、ラベルやエイリアスの不足を報告します。
	 *
	 * @param registrar Paper のコマンドレジストラ
	 */
	public void registerAll(Commands registrar) {
		for (TNexusCommand command : commands) {
			LiteralCommandNode<CommandSourceStack> node = command.build();
			Set<String> registeredLabels = registrar.register(
				node,
				command.description(),
				command.aliases()
			);

			String label = node.getLiteral();

			if (!registeredLabels.contains(label)) {
				this.logger.error("Failed to register command: /{}", label);
				continue;
			}

			for (String alias : command.aliases()) {
				if (!registeredLabels.contains(alias)) {
					this.logger.warn(
						"Alias /{} for /{} was not registered as a plain label; it may already be in use.",
						alias,
						label
					);
				}
			}
		}
	}
}
