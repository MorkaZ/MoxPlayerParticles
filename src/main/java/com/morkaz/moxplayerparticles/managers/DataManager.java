package com.morkaz.moxplayerparticles.managers;

import com.morkaz.moxlibrary.api.ConfigUtils;
import com.morkaz.moxlibrary.api.ServerUtils;
import com.morkaz.moxlibrary.data.ParticleData;
import com.morkaz.moxlibrary.other.moxdata.MoxData;
import com.morkaz.moxplayerparticles.MoxPlayerParticles;
import com.morkaz.moxplayerparticles.data.ParticleSetting;
import com.morkaz.moxplayerparticles.data.PlayerData;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DataManager {

	private MoxPlayerParticles main;
	//          ID    , Data
	public Map<String, PlayerData> playerDataMap = new HashMap<>();
	//          Name  , Data
	public Map<String, ParticleSetting> particleSettingMap = new HashMap<>(); // String to give ability to define own particle settings

	public DataManager(MoxPlayerParticles main) {
		this.main = main;
		reload();
	}

	public void reload(){
		this.loadParticlesData();
		this.purgeOldPlayers();
		new BukkitRunnable() {
			@Override
			public void run() {
				// Getting it into task to make sure that DataManager will be initialized when PlayerData will request it from main instance.
				// Without it, from PlayerData pont, DataManager will be null in main instance.
				loadAllPlayersData();
			}
		}.runTask(main);
	}

	public void purgeOldPlayers(){
		// Config
		if (!main.getConfig().getBoolean("purger.enabled")){
			return;
		}
		Integer configDays = main.getConfig().getInt("purger.max-not-online-days");
		Long minDate = System.currentTimeMillis() - (86400000L * configDays.longValue()); // (current date) - (max time)
		// Query
		String query = "DELETE FROM `"+main.TABLE+"` WHERE `"+main.LAST_LOGIN_COLUMN+"` < "+minDate;
		main.getDatabase().updateSync(query);
	}


	public void loadParticlesData(){
		particleSettingMap.clear();
		// Define default particle data for all particle types
		for (Particle particle : Particle.values()){
			if (particle.toString().contains("LEGACY")){
				continue;
			}
			ParticleData particleData = ConfigUtils.loadParticleData(main.getParticlesConfig(), "default-setting", particle, main);
			ParticleSetting particleSetting = new ParticleSetting(
					particle.toString(),
					main.getConfig().getString("permission-schema").replace("%particle%", particle.toString()),
					particleData
			);
			this.particleSettingMap.put(particle.toString(), particleSetting);
		}
		// Defined own particles data in config
		ConfigurationSection indexes =  main.getParticlesConfig().getConfigurationSection("particles");
		for (String index : indexes.getKeys(false)){
			ParticleData particleData = ConfigUtils.loadParticleData(main.getParticlesConfig(), "particles."+index, main);
			ParticleSetting particleSetting = new ParticleSetting(
					index,
					main.getConfig().getString("permission-schema").replace("%particle%", index),
					particleData
			);
			this.particleSettingMap.put(index, particleSetting);
		}
	}

	public PlayerData loadPlayerData(Player player){
		return this.loadPlayerData(player.getName().toLowerCase());
	}

	public PlayerData loadPlayerData(String playerName){
		String playerID = ServerUtils.getPlayerID(playerName);
		if (playerDataMap.containsKey(playerID)){
			return playerDataMap.get(playerID);
		}
		PlayerData playerData = new PlayerData(playerID, System.currentTimeMillis(), new MoxData(""), false);
		this.playerDataMap.put(playerID, playerData);
		return playerData;
	}

	public void unloadPlayerData(Player player){
		this.unloadPlayerData(player.getName().toLowerCase());
	}

	public void unloadPlayerData(String playerName){
		String playerID = ServerUtils.getPlayerID(playerName);
		if (playerDataMap.containsKey(playerID)){
			playerDataMap.remove(playerID);
		}
	}

	public void loadAllPlayersData(){
		this.playerDataMap.clear();
		String query = "SELECT * FROM `"+main.TABLE+"`";
		ResultSet resultSet = main.getDatabase().getResult(query);
		try {
			while (resultSet.next()) {
				String playerID = resultSet.getString(main.ID_COLUMN);
				String stringData = resultSet.getString(main.DATA_COLUMN);
				Long lastLogin = resultSet.getLong(main.LAST_LOGIN_COLUMN);
				MoxData moxPlayerData = new MoxData(stringData);
				PlayerData playerData = new PlayerData(playerID, lastLogin, moxPlayerData, true);
				this.playerDataMap.put(playerID, playerData);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ParticleSetting getParticleSetting(String particleSettingIndex){
		return this.particleSettingMap.get(particleSettingIndex);
	}

	public PlayerData getPlayerData(String playerID){
		if (playerDataMap.containsKey(playerID)){
			return playerDataMap.get(playerID);
		}
		return new PlayerData(playerID, System.currentTimeMillis(), new MoxData(""), false);
	}


}
