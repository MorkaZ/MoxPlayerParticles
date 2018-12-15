package com.morkaz.moxplayerparticles.listeners;

import com.morkaz.moxlibrary.api.ServerUtils;
import com.morkaz.moxplayerparticles.MoxPlayerParticles;
import com.morkaz.moxplayerparticles.data.ParticleSetting;
import com.morkaz.moxplayerparticles.data.PlayerData;
import com.morkaz.moxplayerparticles.misc.EffectType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockInteractListener  implements Listener {

	private MoxPlayerParticles main;

	public BlockInteractListener(MoxPlayerParticles main) {
		this.main = main;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void blockBreakListener(BlockBreakEvent event) {
		PlayerData playerData = main.getDataManager().getPlayerData(ServerUtils.getPlayerID(event.getPlayer()));
		ParticleSetting particleSetting = playerData.getParticleSetting(EffectType.BLOCK_BREAK);
		if (particleSetting != null){
			particleSetting.setCount(particleSetting.getCount()*3);
			particleSetting.spawn(
					event.getPlayer(),
					event.getBlock().getLocation()
			);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void blockPlaceListener(BlockPlaceEvent event) {
		PlayerData playerData = main.getDataManager().getPlayerData(ServerUtils.getPlayerID(event.getPlayer()));
		ParticleSetting particleSetting = playerData.getParticleSetting(EffectType.BLOCK_PLACE);
		if (particleSetting != null){
			particleSetting.setCount(particleSetting.getCount()*3);
			particleSetting.spawn(
					event.getPlayer(),
					event.getBlock().getLocation().add(0, 0.7d, 0d)
			);
		}
	}


}
