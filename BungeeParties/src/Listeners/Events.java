package Listeners;

import java.util.concurrent.TimeUnit;

import Core.Main;
import Core.Party;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;

public class Events implements Listener {

	ScheduledTask task;
	
	@EventHandler
	public void swapServersEvent(ServerConnectedEvent event) {
		try {
			ProxiedPlayer player = event.getPlayer();
			if(Main.sqlConnector.isPlayerInTable(player) && Main.sqlConnector.isPlayerInParty(player)) {
				Party party = Main.partyPool.getPartyByPlayerSQL(player);
				if(party.getPartyLeader().equals(player)) {
					for(ProxiedPlayer partyplayer : party.getPlayers()) {
						if(!partyplayer.equals(party.getPartyLeader())) {
							task = Main.instance.getProxy().getScheduler().schedule(Main.instance, new Runnable()
							{
						    	public void run()
						    	{
						    		partyplayer.connect(event.getPlayer().getServer().getInfo());
						    		CancelTask();
						    	}
							}, 50, TimeUnit.MILLISECONDS);
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Error while swapping party with leader servers " + e);
		}
	}
	
	@EventHandler
	public void playerDisconnect(PlayerDisconnectEvent event) {
		ProxiedPlayer player = event.getPlayer();
		if(Main.sqlConnector.isPlayerInTable(player) && Main.sqlConnector.isPlayerInParty(player)) {
			Party party = Main.partyPool.getPartyByPlayerSQL(player);
			party.removePlayer(player);
			player.sendMessage(new ComponentBuilder("Reset party status for: " + player.getName()).create());
		}
	}
	
	public void CancelTask() {
		Main.instance.getProxy().getScheduler().cancel(task);
	}
}
