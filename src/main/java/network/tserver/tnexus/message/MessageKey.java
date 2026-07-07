package network.tserver.tnexus.message;

public enum MessageKey {
	COMMAND_INFO("tnexus.command.info"),
	COMMAND_RELOAD_SUCCESS("tnexus.command.reload.success"),
	COMMAND_RELOAD_FAILED("tnexus.command.reload.failed");

	private final String key;

	MessageKey(String key) {
		this.key = key;
	}

	public String key() {
		return this.key;
	}
}
