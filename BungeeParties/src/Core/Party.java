package Core;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
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
		players.add(leader);
		this.partyPoolNumber = poolNumber;
		this.partyName = leader.getDisplayName() + "'s Party";
		this.inactiveTimer = Main.inactiveRemovalTime;
	}
	
	public void addPlayer(ProxiedPlayer player) {
		players.add(player);
		if(!Main.sqlConnector.isPlayerInTable(player)) {
			Main.sqlConnector.addBlankPlayer(player); // New Player :D!
		}
		Main.sqlConnector.setPartyNameForPlayer(player, partyName);
		Main.sqlConnector.setPartyStatusForPlayer(player, true);
		Main.sqlConnector.setPoolNumberForPlayer(player, partyPoolNumber);
		player.sendMessage(new ComponentBuilder("You have joined: " + partyName).create());
	}
	
	public void removePlayer(ProxiedPlayer player) {
		players.remove(player);
		Main.sqlConnector.setPartyNameForPlayer(player, null);
		Main.sqlConnector.setPartyStatusForPlayer(player, false);
		Main.sqlConnector.setPoolNumberForPlayer(player, -1);
		player.sendMessage(new ComponentBuilder("You have left: " + partyName).create());
		if(player.equals(leader) && players.size() > 0) {
			changePartyLeader(players.get(0), "Party Leader has left.");
		} else if(player.equals(leader) && players.isEmpty()) {
			disband();
		}
	}
	
	public void kickPlayer(ProxiedPlayer player) {
		players.remove(player);
		Main.sqlConnector.setPartyNameForPlayer(player, null);
		Main.sqlConnector.setPartyStatusForPlayer(player, false);
		Main.sqlConnector.setPoolNumberForPlayer(player, -1);
		player.sendMessage(new ComponentBuilder("You have been kicked from: " + partyName).create());
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
	
	public void setPartyNameWithFormat(String partyName) {
		partyName = partyName.replace("&", "§");
		this.partyName = partyName;
	}
	
	public void setPartyNameNoFormat(String partyName) {
		this.partyName = partyName;
	}
	
	public String getPartyName() {
		return partyName;
	}
	
	public ProxiedPlayer getPartyLeader() {
		return leader;
	}
	
	public List<ProxiedPlayer> getPlayers() {
		return players;
	}
	
	public void reuse(ProxiedPlayer leader, ArrayList<ProxiedPlayer> players) {
		this.leader = leader;
		this.partyName = leader.getDisplayName() + "'s Party";
		players.add(leader);
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
				Main.sqlConnector.setPartyNameForPlayer(player, "");
				Main.sqlConnector.setPartyStatusForPlayer(player, false);
				Main.sqlConnector.setPoolNumberForPlayer(player, -1);
			}
		}
		Main.instance.getProxy().getConsole().sendMessage(new ComponentBuilder("Party: " + getPartyName() + ChatColor.WHITE +" PN: " + partyPoolNumber + " returned to the PartyPool.").color(ChatColor.WHITE).create());
		players.clear();
		leader = null;
		partyName = "";
		Main.partyPool.returnPartyToPool(this);
	}
}
