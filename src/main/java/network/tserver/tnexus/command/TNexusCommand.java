package network.tserver.tnexus.command;

import java.util.List;

import com.mojang.brigadier.tree.LiteralCommandNode;

import io.papermc.paper.command.brigadier.CommandSourceStack;

/**
 * ブートストラップ時にプラグインへ登録されるコマンドの契約です。
 */
public interface TNexusCommand {
	/**
	 * 登録用の Brigadier コマンドノードを構築します。
	 *
	 * @return ルートのリテラルコマンドノード
	 */
	LiteralCommandNode<CommandSourceStack> build();

	/**
	 * コマンドメタデータに表示する説明文を返します。
	 *
	 * @return 人が読めるコマンド説明
	 */
	String description();

	/**
	 * 同じコマンドへ紐づける別名ラベルを返します。
	 *
	 * @return 不変のエイリアス一覧
	 */
	default List<String> aliases() {
		return List.of();
	}
}
