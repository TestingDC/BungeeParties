package Core;

import Commands.TestCommand;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {

	public static String prefix = "§5[§dBungeeParties§5] §d";
	public static int maxPartyPool = 250; // Max Number of Parties at any Instance.
	public static int inactiveRemovalTime = 600; // In Seconds
	
	@Override
	public void onEnable() {
		this.getProxy().getConsole().sendMessage(new ComponentBuilder(prefix + "BungeeParties §aEnabled.").create());
		this.getProxy().getPluginManager().registerCommand(this, new TestCommand());
	}
	
	@Override
	public void onDisable() {
		this.getProxy().getConsole().sendMessage(new ComponentBuilder(prefix + "BungeeParties §cDisabled.").create());
	}
}
