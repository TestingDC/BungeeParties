package Commands;

import Core.Main;
import Core.Party;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PartyCommand extends Command {

	public PartyCommand() {
		super("party");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(sender instanceof ProxiedPlayer) {
			ProxiedPlayer player = (ProxiedPlayer) sender;
			Party party = null;
			switch(args.length) {
			case 1:
				switch(args[0]) {
				case "help":
					player.sendMessage(new ComponentBuilder("-- Party Help --").color(ChatColor.RED).create());
					player.sendMessage(new ComponentBuilder("/Party Create").color(ChatColor.RED).create());
					player.sendMessage(new ComponentBuilder("/Party Disband").color(ChatColor.RED).create());
					player.sendMessage(new ComponentBuilder("/Party Invite (Player)").color(ChatColor.RED).create());
					player.sendMessage(new ComponentBuilder("/Party Kick (Player)").color(ChatColor.RED).create());
					break;
				case "create":
					party = Main.partyPool.newParty(player);
					if(party == null) {
						player.sendMessage(new ComponentBuilder("Party Failed to Create. (Already in a Party?)").color(ChatColor.RED).create());
					} else {
						player.sendMessage(new ComponentBuilder("Party Created.").color(ChatColor.YELLOW).create());
					}
					break;
				case "disband":
					party = Main.partyPool.getPartyByPlayerSQL(player);
					if(party == null) {
						player.sendMessage(new ComponentBuilder("You are not in a party.").color(ChatColor.RED).create());
					} else {
						if(party.getPartyLeader().equals(player)) {
							party.disband("Disbanded by Leader.");
							player.sendMessage(new ComponentBuilder("Party Disbanded.").color(ChatColor.YELLOW).create());
						} else {
							player.sendMessage(new ComponentBuilder("You are not the leader of this party.").color(ChatColor.RED).create());
						}
					}
					break;
				}
				break;
			case 2:
				switch(args[0]) {
				case "create":
					party = Main.partyPool.newParty(player);
					if(party == null) {
						player.sendMessage(new ComponentBuilder("Party Failed to Create. (Already in a Party?)").color(ChatColor.RED).create());
					} else {
						party.setPartyNameWithFormat(args[1]);
						sender.sendMessage(new ComponentBuilder("Party Created with the Name: " + party.getPartyName()).color(ChatColor.YELLOW).create());
					}
					break;
				case "invite":
					party = Main.partyPool.getPartyByPlayerSQL(player);
					ProxiedPlayer toSend = Main.instance.getProxy().getPlayer(args[1]);
					if(toSend != null) {
						Main.inviteHandler.sendInvite(toSend, party);
					} else {
						sender.sendMessage(new ComponentBuilder("No user found with that name.").color(ChatColor.RED).create());
					}
					break;
				case "accept":
					ProxiedPlayer leader = Main.instance.getProxy().getPlayer(args[1]);
					party = Main.partyPool.getPartyByPlayerSQL(leader);
					if(!Main.sqlConnector.isPlayerInParty(player)) {
						party.addPlayer(player);
						Main.inviteHandler.acceptedInvite(player);
					} else {
						sender.sendMessage(new ComponentBuilder("Already in a Party. Leave your current party to join a new one.").color(ChatColor.RED).create());
					}
					break;
				case "kick":
					party = Main.partyPool.getPartyByPlayerSQL(player);
					if(party == null) {
						player.sendMessage(new ComponentBuilder("You are not in a party.").color(ChatColor.RED).create());
					} else {
						if(party.getPartyLeader().equals(player)) {
							ProxiedPlayer temp = Main.instance.getProxy().getPlayer(args[1]);
							if(temp != null) {
								if(temp.equals(player)) {
									player.sendMessage(new ComponentBuilder("You cannot kick yourself. To disband type /party disband").color(ChatColor.RED).create());
								} else {
									party.kickPlayer(temp);
									player.sendMessage(new ComponentBuilder("Kicked " + temp.getDisplayName()).color(ChatColor.YELLOW).create());
								}
							} else {
								player.sendMessage(new ComponentBuilder("No user found with that name.").color(ChatColor.RED).create());
							}
						} else {
							player.sendMessage(new ComponentBuilder("You are not the leader of this party.").color(ChatColor.RED).create());
						}
					}
					break;
				}
				break;
			}
		}
	}
}
