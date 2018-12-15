package com.morkaz.moxplayerparticles.listeners;

import com.morkaz.moxlibrary.api.ServerUtils;
import com.morkaz.moxplayerparticles.MoxPlayerParticles;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

	private MoxPlayerParticles main;

	public JoinQuitListener(MoxPlayerParticles main) {
		this.main = main;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void joinListener(PlayerJoinEvent event) {
		main.getDataManager().loadPlayerData(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void quitListener(PlayerQuitEvent event) {
		String playerID = ServerUtils.getPlayerID(event.getPlayer());
		if (!main.getDataManager().getPlayerData(playerID).isInDatabase()){
			main.getDataManager().unloadPlayerData(event.getPlayer());
		}

	}

}
