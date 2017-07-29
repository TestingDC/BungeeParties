package Core;

import java.sql.Connection;
import java.util.concurrent.TimeUnit;

import Commands.PartyCommand;
import Listeners.Events;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class Main extends Plugin {

	public static Main instance = null;
	
	public static PartySQLConnector sqlConnector = new PartySQLConnector();
	public static PartyPool partyPool = new PartyPool();
	public static InviteHandler inviteHandler = new InviteHandler();
	private static ScheduledTask task = null;
	
	public static String prefix = "§5[§dBungeeParties§5] §d";
	public static int maxPartyPool = 250; // Max Number of Parties at any Instance.
	public static int inactiveRemovalTime = 600; // In Seconds
	public static int inviteTimeTillExpire = 60;
	
	@Override
	public void onEnable() {
		Main.instance = this;
		Connection connect = sqlConnector.connect();
		if(connect != null) {
			sqlConnector.createTable();
			getProxy().getConsole().sendMessage(new ComponentBuilder(prefix + "BungeeParties §aEnabled.").create());
			getProxy().getPluginManager().registerListener(this, new Events());
			getProxy().getPluginManager().registerCommand(this, new PartyCommand());
			startLoop();
		} else {
			getProxy().getConsole().sendMessage(new ComponentBuilder(prefix + "BungeeParties §cFailed to Connect to MySQL. This plugin will not initialize until it can connect to MySQL.").create());
		}
	}

	@Override
	public void onDisable() {
		getProxy().getConsole().sendMessage(new ComponentBuilder(prefix + "BungeeParties §eStopping Loop.").create());
		task.cancel();
		getProxy().getConsole().sendMessage(new ComponentBuilder(prefix + "BungeeParties §eDisbanding all Parties.").create());
		partyPool.disbandParties();
		getProxy().getConsole().sendMessage(new ComponentBuilder(prefix + "BungeeParties §cDisabled.").create());
	}
	
	public void startLoop() {
		task = Main.instance.getProxy().getScheduler().schedule(Main.instance, new Runnable()
		{
	    	public void run()
	    	{
	    		inviteHandler.tick();
	    	}
		}, 1, TimeUnit.SECONDS);
	}
}
