package Core;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Party {

	int inactiveTimer = -1;
	int partyPoolNumber = -1;
	ProxiedPlayer leader = null;
	List<ProxiedPlayer> players = null;
	String partyName = "";
	
	public Party(ProxiedPlayer leader, ArrayList<ProxiedPlayer> players, int poolNumber) {
		this.leader = leader;
		this.players = players;
		this.partyPoolNumber = poolNumber;
		this.partyName = leader.getDisplayName() + "'s Party";
		this.inactiveTimer = Main.inactiveRemovalTime;
	}
	
	public Party(ProxiedPlayer leader, int poolNumber) {
		this.leader = leader;
		this.players = new ArrayList<ProxiedPlayer>();
		this.partyPoolNumber = poolNumber;
		this.partyName = leader.getDisplayName() + "'s Party";
		this.inactiveTimer = Main.inactiveRemovalTime;
	}
	
	public void addPlayer(ProxiedPlayer player) {
		players.add(player);
		player.sendMessage(new ComponentBuilder("You have joined: " + partyName).create());
	}
	
	public void removePlayer(ProxiedPlayer player) {
		players.remove(player);
		player.sendMessage(new ComponentBuilder("You have left: " + partyName).create());
		if(player.equals(leader)) {
			changePartyLeader(players.get(0), "Party Leader has left.");
		}
	}
	
	public boolean containsPlayer(ProxiedPlayer player) {
		for(ProxiedPlayer partyplayer : players) {
			if(partyplayer.equals(player)) {
				return true;
			}
		}
		return false;
	}
	
	public void changePartyLeader(ProxiedPlayer player) {
		changePartyLeader(player, null);
	}
	
	public void changePartyLeader(ProxiedPlayer player, String reason) {
		for(ProxiedPlayer partyplayer : players) {
			if(partyplayer.equals(player)) {
				leader = player;
				partyName = leader.getDisplayName() + "'s Party";
				if(reason != null) {
					leader.sendMessage(new ComponentBuilder("You are now the new Party Leader. Reason: " + reason).create());
				} else {
					leader.sendMessage(new ComponentBuilder("You are now the new Party Leader.").create());
				}
			}
		}
	}
	
	public void update() {
		// Something to do with inactive timer :L
		if(!leader.isConnected() && players.isEmpty()) {
			disband();
		}
	}
	
	public void reuse(ProxiedPlayer leader, ArrayList<ProxiedPlayer> players) {
		this.leader = leader;
		this.partyName = leader.getDisplayName() + "'s Party";
		if(players != null) {
			this.players = players;
		}
		this.inactiveTimer = Main.inactiveRemovalTime;
	}
	
	public void disband() {
		disband(null);
	}
	
	public void disband(String reason) {
		if(reason != null) {
			for(ProxiedPlayer player : players) {
				player.sendMessage(new ComponentBuilder("Party Disbanded. Reason: " + reason).create());
			}
		}
		players.clear();
		leader = null;
		partyName = "";
		// return to party pool. //POP.
	}
}
