package com.morkaz.moxplayerparticles.listeners;

import com.morkaz.moxlibrary.api.ServerUtils;
import com.morkaz.moxplayerparticles.MoxPlayerParticles;
import com.morkaz.moxplayerparticles.data.ParticleSetting;
import com.morkaz.moxplayerparticles.data.PlayerData;
import com.morkaz.moxplayerparticles.misc.EffectType;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockInteractListener  implements Listener {

	private MoxPlayerParticles main;

	public BlockInteractListener(MoxPlayerParticles main) {
		this.main = main;
		main.getServer().getPluginManager().registerEvents(this, main);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void blockBreakListener(BlockBreakEvent event) {
		PlayerData playerData = main.getDataManager().getPlayerData(ServerUtils.getPlayerID(event.getPlayer()));
		ParticleSetting particleSetting = playerData.getParticleSetting(EffectType.BLOCK_BREAK);
		if (particleSetting != null){
			if (particleSetting.getCount() < 10 && particleSetting.getParticle() != Particle.EXPLOSION_HUGE
					&& particleSetting.getParticle() != Particle.EXPLOSION_LARGE && particleSetting.getParticle() != Particle.EXPLOSION_NORMAL){
				particleSetting.setCount(particleSetting.getCount()+10);
			}
			if (particleSetting.getOffsetX() < 0.4d){
				particleSetting.setOffsetX(particleSetting.getOffsetX()+0.3d);
			}
			if (particleSetting.getOffsetZ() < 0.4d){
				particleSetting.setOffsetZ(particleSetting.getOffsetZ()+0.3d);
			}
			if (particleSetting.getOffsetY() < 0.4d){
				particleSetting.setOffsetY(particleSetting.getOffsetY()+0.3d);
			}
			particleSetting.spawn(
					event.getPlayer(),
					event.getBlock().getLocation().add(0.5, 0.25d, 0.5d)
			);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void blockPlaceListener(BlockPlaceEvent event) {
		PlayerData playerData = main.getDataManager().getPlayerData(ServerUtils.getPlayerID(event.getPlayer()));
		ParticleSetting particleSetting = playerData.getParticleSetting(EffectType.BLOCK_PLACE);
		if (particleSetting != null){
			if (particleSetting.getCount() < 10 && particleSetting.getParticle() != Particle.EXPLOSION_HUGE
					&& particleSetting.getParticle() != Particle.EXPLOSION_LARGE && particleSetting.getParticle() != Particle.EXPLOSION_NORMAL){
				particleSetting.setCount(particleSetting.getCount()+10);
			}
			if (particleSetting.getOffsetX() < 0.4d){
				particleSetting.setOffsetX(particleSetting.getOffsetX()+0.3d);
			}
			if (particleSetting.getOffsetZ() < 0.4d){
				particleSetting.setOffsetZ(particleSetting.getOffsetZ()+0.3d);
			}
			if (particleSetting.getOffsetY() < 0.4d){
				particleSetting.setOffsetY(particleSetting.getOffsetY()+0.3d);
			}
			particleSetting.spawn(
					event.getPlayer(),
					event.getBlock().getLocation().add(0.5, 0.75d, 0.5d)
			);
		}
	}


}
