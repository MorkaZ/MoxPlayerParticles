package com.morkaz.moxplayerparticles.listeners;

import com.morkaz.moxlibrary.api.ServerUtils;
import com.morkaz.moxplayerparticles.MoxPlayerParticles;
import com.morkaz.moxplayerparticles.data.ParticleSetting;
import com.morkaz.moxplayerparticles.data.PlayerData;
import com.morkaz.moxplayerparticles.misc.EffectType;
import org.bukkit.Particle;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class EntityDamageListener implements Listener {

	private MoxPlayerParticles main;

	public EntityDamageListener(MoxPlayerParticles main) {
		this.main = main;
		main.getServer().getPluginManager().registerEvents(this, main);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void blockBreakListener(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof HumanEntity){
			Player player = (Player)event.getDamager();
			PlayerData playerData = main.getDataManager().getPlayerData(ServerUtils.getPlayerID(player));
			ParticleSetting particleSetting = playerData.getParticleSetting(EffectType.ENTITY_DAMAGE);
			if (particleSetting != null){
				if (event.getEntity() instanceof LivingEntity){
					LivingEntity victim = (LivingEntity)event.getEntity();
					Double preHealth = victim.getHealth();
					new BukkitRunnable(){
						@Override
						public void run() {
							if (victim.getHealth() != preHealth){
								if (particleSetting.getCount() < 12 && particleSetting.getParticle() != Particle.EXPLOSION_HUGE
										&& particleSetting.getParticle() != Particle.EXPLOSION_LARGE && particleSetting.getParticle() != Particle.EXPLOSION_NORMAL){
									particleSetting.setCount(particleSetting.getCount()+12);
								}
								if (particleSetting.getOffsetX() < 0.3d){
									particleSetting.setOffsetX(particleSetting.getOffsetX()+0.3d);
								}
								if (particleSetting.getOffsetZ() < 0.3d){
									particleSetting.setOffsetZ(particleSetting.getOffsetZ()+0.3d);
								}
								if (particleSetting.getOffsetY() < 0.2d){
									particleSetting.setOffsetY(particleSetting.getOffsetY()+0.2d);
								}
								particleSetting.spawn(
										player,
										event.getEntity().getLocation().add(0d, 0.75d, 0d)
								);
							}
						}
					}.runTaskLater(main, 1L);
				}
			}
		}

	}
}
