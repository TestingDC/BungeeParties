package Core;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class InviteHandler {

	List<Invite> invites = new ArrayList<Invite>();
	
	public void sendInvite(ProxiedPlayer player, Party party) {
		boolean send = true;
		for(Invite invite : invites) {
			if(invite.inviteTo.equals(player) && invite.toJoin.equals(party)) {
				party.leader.sendMessage(new ComponentBuilder("This player already has a pending invite to: " + party.getPartyName()).color(ChatColor.RED).create());
				send = false;
			}
		}
		if(player.equals(party.getPartyLeader())) {
			party.leader.sendMessage(new ComponentBuilder("You can't invite yourself.").color(ChatColor.RED).create());
			send = false;
		}
		if(send) {
			Invite invite = new Invite(player, party);
			invites.add(invite);
			party.leader.sendMessage(new ComponentBuilder("Invite sent to: " + player.getDisplayName()).color(ChatColor.YELLOW).create());
		}
	}
	
	public void removeInvite(Invite invite) {
		invites.remove(invite);
	}
	
	public void acceptedInvite(ProxiedPlayer player) {
		Invite temp = null;
		for(Invite invite : invites) {
			if(invite.inviteTo.equals(player)) {
				temp = invite;
			}
		}
		temp.accepted();
	}
	
	public void tick() {
		for(Invite invite : invites) {
			invite.tick();
		}
	}
}

class Invite {
	
	boolean expired = false;
	int timeLeft = -1;
	Party toJoin = null;
	ProxiedPlayer inviteTo = null;
	
	public Invite(ProxiedPlayer player, Party party) {
		this.timeLeft = Main.inviteTimeTillExpire;
		this.inviteTo = player;
		this.toJoin = party;
		player.sendMessage(new ComponentBuilder("You have been invited to a party: " + toJoin.getPartyName() + " Click me to Join! Expires in " + Main.inviteTimeTillExpire + " Seconds.").event(new ClickEvent(Action.RUN_COMMAND, "/party accept " + party.getPartyLeader().getName())).color(ChatColor.YELLOW).create());
	}
	
	public void tick() {
		timeLeft--;
		if(timeLeft <= 0) {
			toJoin.getPartyLeader().sendMessage(new ComponentBuilder("Invite to " + inviteTo.getDisplayName() + " has Expired.").color(ChatColor.RED).create());
			timeLeft = -1;
			toJoin = null;
			inviteTo = null;
			expired = true;
			Main.inviteHandler.removeInvite(this);
		}
	}
	
	public void accepted() {
		toJoin.getPartyLeader().sendMessage(new ComponentBuilder("Invite to " + inviteTo.getDisplayName() + " was accepted!").color(ChatColor.GREEN).create());
		timeLeft = -1;
		toJoin = null;
		inviteTo = null;
		expired = true;
		Main.inviteHandler.removeInvite(this);
	}
	
	public boolean isExpired() {
		return expired;
	}
}