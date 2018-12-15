package com.morkaz.moxplayerparticles.listeners;

import com.morkaz.moxlibrary.api.LocationUtils;
import com.morkaz.moxlibrary.api.ServerUtils;
import com.morkaz.moxplayerparticles.MoxPlayerParticles;
import com.morkaz.moxplayerparticles.data.ParticleSetting;
import com.morkaz.moxplayerparticles.data.PlayerData;
import com.morkaz.moxplayerparticles.misc.EffectType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class MoveListener implements Listener {

	private MoxPlayerParticles main;
	private Map<Player, Long> lastMovedMap = new HashMap<>();
	private BukkitTask bukkitTask;

	public MoveListener(MoxPlayerParticles main) {
		this.main = main;
		startTask();
		main.getServer().getPluginManager().registerEvents(this, main);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void moveListener(PlayerMoveEvent event) {
		lastMovedMap.put(event.getPlayer(), System.currentTimeMillis());
		PlayerData playerData = main.getDataManager().getPlayerData(ServerUtils.getPlayerID(event.getPlayer()));
		ParticleSetting particleSetting = playerData.getParticleSetting(EffectType.WALK);
		if (particleSetting != null){
			Location behindLocation = LocationUtils.getLocationBehindPlayer(event.getPlayer(), 1d);
			particleSetting.spawn(event.getPlayer(), behindLocation);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void quitListener(PlayerQuitEvent event) {
		lastMovedMap.remove(event.getPlayer());
	}

	public BukkitTask startTask(){
		BukkitTask bukkitTask = new BukkitRunnable(){
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()){
					if (lastMovedMap.containsKey(player) && lastMovedMap.get(player) < (System.currentTimeMillis()-4000L)){
						lastMovedMap.remove(player);
					}
				}
				for (Player player : Bukkit.getOnlinePlayers()){
					if (lastMovedMap.containsKey(player)){
						continue;
					}
					PlayerData playerData = main.getDataManager().getPlayerData(ServerUtils.getPlayerID(player));
					ParticleSetting particleSetting = playerData.getParticleSetting(EffectType.IDLE);
					if (particleSetting != null){
						particleSetting.setCount(particleSetting.getCount()*2);
						Location location = player.getEyeLocation().add(0d, 0.5d, 0d);
						for (int i = 0; i < 6; i++){
							Location spawnLocation = new Location(
									location.getWorld(),
									location.getX()+ThreadLocalRandom.current().nextDouble(-0.1d, 0.1d),
									location.getY(),
									location.getZ()+ThreadLocalRandom.current().nextDouble(-0.1d, 0.1d)
							);
							particleSetting.spawn(player, spawnLocation);
						}
					}
				}
			}
		}.runTaskTimer(main, 20L, 10L);
		return bukkitTask;
	}

}
