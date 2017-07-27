package Core;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PartyPool {

	List<Party> inactiveParties = new ArrayList<Party>();
	List<Party> parties = new ArrayList<Party>();
	int poolNumber = 0;
	
	public void newParty(ProxiedPlayer player) {
		if(!inactiveParties.isEmpty()) {
			Party party = inactiveParties.get(0);
			party.reuse(player, null);
			inactiveParties.remove(0);
		} else {
			if(poolNumber >= Main.maxPartyPool) {
				player.sendMessage(new ComponentBuilder("Sorry we can't make your party right now. Party Pool is Full.").create());
				// Sorry man we cant create your party right now. :L Parties are completely full; // TODO
			} else {
				Party party = new Party(player, poolNumber);
				parties.add(party);
			}
		}
	}
	
	public Party getPartyByPlayer(ProxiedPlayer player) {
		for(Party party : parties) {
			if(party.containsPlayer(player)) {
				return party;
			}
		}
		return null;
	}
	
	//public Party getPartyByPlayerSQL() {
		// request SQL to send us Player Information!
	//}
	
	public void tickParties() {
		for(Party party : parties) {
			party.update();
		}
	}
}
