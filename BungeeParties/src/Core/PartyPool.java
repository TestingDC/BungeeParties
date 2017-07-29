package Core;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PartyPool {

	List<Party> inactiveParties = new ArrayList<Party>();
	List<Party> parties = new ArrayList<Party>();
	int poolNumber = 0;
	
	public Party newParty(ProxiedPlayer player) {
		if(!Main.sqlConnector.isPlayerInParty(player)) {
			if(!inactiveParties.isEmpty()) {
				Main.instance.getProxy().getConsole().sendMessage(new ComponentBuilder("Reusing Inactive Party to Build new Party.").color(ChatColor.WHITE).create());
				Party party = inactiveParties.get(0);
				party.reuse(player, null);
				inactiveParties.remove(0);
				if(!Main.sqlConnector.isPlayerInTable(player)) {
					Main.sqlConnector.addBlankPlayer(player); // New Player :D!
				}
				Main.sqlConnector.setPartyNameForPlayer(player, party.partyName);
				Main.sqlConnector.setPartyStatusForPlayer(player, true);
				Main.sqlConnector.setPoolNumberForPlayer(player, party.partyPoolNumber);
				poolNumber++;
				return party;
			} else {
				if(poolNumber >= Main.maxPartyPool) {
					player.sendMessage(new ComponentBuilder("Sorry we can't make your party right now. Party Pool is Full.").create());
					// Sorry man we cant create your party right now. :L Parties are completely full; // TODO
					return null;
				} else {
					Main.instance.getProxy().getConsole().sendMessage(new ComponentBuilder("Creating a new Party.").color(ChatColor.WHITE).create());
					Party party = new Party(player, poolNumber);
					parties.add(party);
					if(!Main.sqlConnector.isPlayerInTable(player)) {
						Main.sqlConnector.addBlankPlayer(player); // New Player :D!
					}
					Main.sqlConnector.setPartyNameForPlayer(player, party.partyName);
					Main.sqlConnector.setPartyStatusForPlayer(player, true);
					Main.sqlConnector.setPoolNumberForPlayer(player, party.partyPoolNumber);
					poolNumber++;
					return party;
				}
			}
		} else {
			return null;
		}
	}
	
	public void returnPartyToPool(Party party) {
		inactiveParties.add(party);
	}
	
	public void joinParty(ProxiedPlayer player, String partyName) {
		Party party = parties.get(Main.sqlConnector.getPoolNumberFromPartyName(partyName));
		party.addPlayer(player);
	}
	
	public void leaveParty(ProxiedPlayer player, String partyName) {
		Party party = parties.get(Main.sqlConnector.getPoolNumberFromPartyName(partyName));
		party.removePlayer(player);
	}
	
	public Party getPartyByPlayerSQL(ProxiedPlayer player) {
		return parties.get(Main.sqlConnector.getPoolNumberFromPlayer(player));
	}
	
	public void disbandParties() {
		for(Party party : parties) {
			if(party.leader != null) {
				party.disband("Server Shutdown");
			}
		}
	}
}
