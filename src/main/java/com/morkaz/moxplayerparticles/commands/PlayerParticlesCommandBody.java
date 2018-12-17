package com.morkaz.moxplayerparticles.commands;

import com.morkaz.moxlibrary.api.ServerUtils;
import com.morkaz.moxlibrary.api.ToolBox;
import com.morkaz.moxplayerparticles.MoxPlayerParticles;
import com.morkaz.moxplayerparticles.data.ParticleSetting;
import com.morkaz.moxplayerparticles.data.PlayerData;
import com.morkaz.moxplayerparticles.misc.EffectType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/*
Perms:
	mox.playerparticles.set
	mox.playerparticles.set.others
	mox.playerparticles.remove
	mox.playerparticles.remove.others
 */

public class PlayerParticlesCommandBody implements CommandExecutor, TabCompleter {

	private MoxPlayerParticles main;

	public PlayerParticlesCommandBody(MoxPlayerParticles main){
		this.main = main;
	}

	private void sendHelpMessage(CommandSender sender, String alias) {
		ServerUtils.sendMessage(sender, main.getMessagesConfig().getString("misc.separator"));
		ServerUtils.sendMessage(sender, " ");
		ServerUtils.sendMessage(sender, " &7- &9/"+alias+" set &3<effect.type> <particle.index> &f- &b"+main.getMessagesConfig().getString("help-outputs.set"));
		ServerUtils.sendMessage(sender, " &7- &9/"+alias+" remove &3<effect.type> &f- &b"+main.getMessagesConfig().getString("help-outputs.remove"));
		if (sender.hasPermission("mox.playerparticles.set.others")){
			ServerUtils.sendMessage(sender, " &7- &9/"+alias+" setp &3<player> <effect.type> <particle.index> &f- &b"+main.getMessagesConfig().getString("help-outputs.set"));
		}
		if (sender.hasPermission("mox.playerparticles.remove.others")){
			ServerUtils.sendMessage(sender, " &7- &9/"+alias+" removep &3<player> <effect.type> &f- &b"+main.getMessagesConfig().getString("help-outputs.remove-other-player"));
		}
		ServerUtils.sendMessage(sender, " ");
		ServerUtils.sendMessage(sender, main.getMessagesConfig().getString("misc.separator"));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] argsArray) {
		List<String> args = Arrays.asList(argsArray);
		if (args.size() >= 1) {
			if (args.get(0).equalsIgnoreCase("set")) {
				if (!(sender instanceof Player)) {
					ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("usage-outputs.player-command-only"));
					return true;
				}
				if (!sender.hasPermission("mox.playerparticles.set")) {
					ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("usage-outputs.no-permission"));
					return true;
				}
				if (args.size() == 1) {
					ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("usage-outputs.bad-command-usage")
							.replace("%command%", "&9/" + alias + " " + args.get(0) + " &3<effect.type&d[?]&3> &3<particle.index&d[?]>")
					);
					return true;
				}
				String effectTypeName = args.get(1).toUpperCase();
				if (!ToolBox.toStringList(Arrays.asList(EffectType.values())).contains(effectTypeName)) {
					ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("usage-outputs.effect-type-not-exist")
							.replace("%effects%", Arrays.toString(EffectType.values()).replace("[", "").replace("]", ""))
					);
					return true;
				}
				EffectType effectType = EffectType.valueOf(effectTypeName);
				if (args.size() == 2) {
					ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("usage-outputs.bad-command-usage")
							.replace("%command%", "&9/" + alias + " " + args.get(0) + " &3<effect.type&a[" + args.get(1) + "]&3> &3<particle.index&d[?]>")
					);
					return true;
				}
				String particleIndex = args.get(2).toUpperCase();
				ParticleSetting particleSetting = main.getDataManager().getParticleSetting(particleIndex);
				if (particleSetting == null) {
					ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("usage-outputs.particle-index-null")
							.replace("%effect%", args.get(2))
					);
					return true;
				}
				Player player = (Player) sender;
				if (!player.hasPermission(particleSetting.getPermission())){
					ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("usage-outputs.no-permission-for-particle"));
					return true;
				}
				String playerID = ServerUtils.getPlayerID(player);
				PlayerData playerData = main.getDataManager().getPlayerData(playerID);
				playerData.setParticleEffect(effectType, particleSetting);
				ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("command-output.particle-effect-set")
						.replace("%effect%", effectTypeName)
						.replace("%index%", particleIndex)
				);
				return true;
			} else if (args.get(0).equalsIgnoreCase("remove")){
				if (!(sender instanceof Player)) {
					ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("usage-outputs.player-command-only"));
					return true;
				}
				if (!sender.hasPermission("mox.playerparticles.set")) {
					ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("usage-outputs.no-permission"));
					return true;
				}
				if (args.size() == 1) {
					ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("usage-outputs.bad-command-usage")
							.replace("%command%", "&9/" + alias + " " + args.get(0) + " &3<effect.type&d[?]&3>")
					);
					return true;
				}
				String effectTypeName = args.get(1).toUpperCase();
				if (!ToolBox.toStringList(Arrays.asList(EffectType.values())).contains(effectTypeName)) {
					ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("usage-outputs.effect-type-not-exist")
							.replace("%effects%", Arrays.toString(EffectType.values()).replace("[", "").replace("]", ""))
					);
					return true;
				}
				EffectType effectType = EffectType.valueOf(effectTypeName);
				Player player = (Player) sender;
				String playerID = ServerUtils.getPlayerID(player);
				PlayerData playerData = main.getDataManager().getPlayerData(playerID);
				playerData.removeParticleEffect(effectType);
				ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("command-output.particle-effect-removed")
						.replace("%effect%", effectTypeName)
				);
				return true;
			} else if (args.get(0).equalsIgnoreCase("setp")){
				if (!sender.hasPermission("mox.playerparticles.set.others")){
					ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("usage-outputs.no-permission"));
					return true;
				}
				if (args.size() == 1){
					ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("usage-outputs.bad-command-usage")
							.replace("%command%", "&9/"+alias+" "+args.get(0)+" &3<player&d[?]&3> &3<effect.type&d[?]&3> &3<particle.index&d[?]>")
					);
					return true;
				}
				if (args.get(1).length() > 16){
					ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("usage-outputs.nick-too-long"));
					return true;
				}
				String playerName = args.get(1);
				String playerID = ServerUtils.getPlayerID(playerName);
				if (args.size() == 2){
					ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("usage-outputs.bad-command-usage")
							.replace("%command%", "&9/"+alias+" "+args.get(0)+" &3<player&a["+args.get(1)+"]&3> &3<effect.type&d[?]&3> &3<particle.index&d[?]>")
					);
					return true;
				}
				String effectTypeName = args.get(2).toUpperCase();
				if (!ToolBox.toStringList(Arrays.asList(EffectType.values())).contains(effectTypeName)){
					ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("usage-outputs.effect-type-not-exist")
							.replace("%effects%", Arrays.toString(EffectType.values()).replace("[", "").replace("]", ""))
					);
					return true;
				}
				EffectType effectType = EffectType.valueOf(effectTypeName);
				if (args.size() == 3){
					ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("usage-outputs.bad-command-usage")
							.replace("%command%", "&9/"+alias+" "+args.get(0)+" &3<player&a["+args.get(1)+"]&3> &3<effect.type&a["+args.get(2)+"]&3> &3<particle.index&d[?]>")
					);
					return true;
				}
				String particleIndex = args.get(3).toUpperCase();
				ParticleSetting particleSetting = main.getDataManager().getParticleSetting(particleIndex);
				if (particleSetting == null){
					ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("usage-outputs.particle-index-null")
							.replace("%effect%", args.get(3))
					);
					return true;
				}
				PlayerData playerData = main.getDataManager().getPlayerData(playerID);
				playerData.setParticleEffect(effectType, particleSetting);
				ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("command-output.particle-effect-set-other-player")
						.replace("%player%", playerName)
						.replace("%effect%", effectTypeName)
						.replace("%index%", particleIndex)
				);
				return true;
			} else if (args.get(0).equalsIgnoreCase("removep")){
				if (!sender.hasPermission("mox.playerparticles.remove.others")){
					ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("usage-outputs.no-permission"));
					return true;
				}
				if (args.size() == 1){
					ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("usage-outputs.bad-command-usage")
							.replace("%command%", "&9/"+alias+" "+args.get(0)+" &3<player&d[?]&3> &3<effect.type&d[?]&3>")
					);
					return true;
				}
				if (args.get(1).length() > 16){
					ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("usage-outputs.nick-too-long"));
					return true;
				}
				String playerName = args.get(1);
				String playerID = ServerUtils.getPlayerID(playerName);
				if (args.size() == 2){
					ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("usage-outputs.bad-command-usage")
							.replace("%command%", "&9/"+alias+" "+args.get(0)+" &3<player&a["+args.get(1)+"]&3> &3<effect.type&d[?]&3> ")
					);
					return true;
				}
				String effectTypeName = args.get(2).toUpperCase();
				if (!ToolBox.toStringList(Arrays.asList(EffectType.values())).contains(effectTypeName)){
					ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("usage-outputs.effect-type-not-exist")
							.replace("%effects%", Arrays.toString(EffectType.values()).replace("[", "").replace("]", ""))
					);
					return true;
				}
				EffectType effectType = EffectType.valueOf(effectTypeName);
				PlayerData playerData = main.getDataManager().getPlayerData(playerID);
				playerData.removeParticleEffect(effectType);
				ServerUtils.sendMessage(sender, main.getPrefix(), main.getMessagesConfig().getString("command-output.particle-effect-removed-other-player")
						.replace("%player%", playerName)
						.replace("%effect%", effectTypeName)
				);
				return true;
			} else {
				this.sendHelpMessage(sender, alias);
				return true;
			}
		} else {
			this.sendHelpMessage(sender, alias);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String commandAlias, String[] argsArray) {
		List<String> args = Arrays.asList(argsArray);
		if (args.size() == 1){
			if (sender instanceof Player){
				return Arrays.asList("set", "remove");
			} else {
				return Arrays.asList("setp", "removep");
			}
		} else if (args.size() == 2){
			if (args.get(0).equalsIgnoreCase("set")){
				List<String> list = ToolBox.toStringList(EffectType.values());
				list.removeIf(s -> !s.startsWith(args.get(1).toUpperCase()));
				return list;
			} else if (args.get(0).equalsIgnoreCase("remove")){
				List<String> list = ToolBox.toStringList(EffectType.values());
				list.removeIf(s -> !s.startsWith(args.get(1).toUpperCase()));
				return list;
			} else if (args.get(0).equalsIgnoreCase("removep")) {
				return null;
			} else if (args.get(0).equalsIgnoreCase("setp")) {
				return null;
			}
		} else if (args.size() == 3) {
			if (args.get(0).equalsIgnoreCase("set")) {
				List<String> list = new ArrayList();
				list.addAll(main.getDataManager().particleSettingMap.keySet());
				list.removeIf(s -> !s.startsWith(args.get(2).toUpperCase()));
				return list;
			} else if (args.get(0).equalsIgnoreCase("remove")) {
				List<String> list = new ArrayList();
				list.addAll(main.getDataManager().particleSettingMap.keySet());
				list.removeIf(s -> !s.startsWith(args.get(2).toUpperCase()));
				return list;
			} else if (args.get(0).equalsIgnoreCase("removep")) {
				List<String> list = ToolBox.toStringList(EffectType.values());
				list.removeIf(s -> !s.startsWith(args.get(2).toUpperCase()));
				return list;
			} else if (args.get(0).equalsIgnoreCase("setp")) {
				List<String> list = ToolBox.toStringList(EffectType.values());
				list.removeIf(s -> !s.startsWith(args.get(2).toUpperCase()));
				return list;
			}
		} else if (args.size() == 4){
			if (args.get(0).equalsIgnoreCase("setp")) {
				List<String> list = new ArrayList();
				list.addAll(main.getDataManager().particleSettingMap.keySet());
				list.removeIf(s -> !s.startsWith(args.get(3).toUpperCase()));
				return list;
			} else if (args.get(0).equalsIgnoreCase("removep")) {
				List<String> list = new ArrayList();
				list.addAll(main.getDataManager().particleSettingMap.keySet());
				list.removeIf(s -> !s.startsWith(args.get(3).toUpperCase()));
				return list;
			} else {
				return Arrays.asList(""); // To not display player names by default
			}
		}
		return null;
	}
}
