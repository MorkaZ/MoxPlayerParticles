package com.morkaz.moxplayerparticles.managers;

import com.morkaz.moxlibrary.api.ServerUtils;
import com.morkaz.moxlibrary.other.moxdata.MoxData;
import com.morkaz.moxplayerparticles.MoxPlayerParticles;
import com.morkaz.moxplayerparticles.data.ParticleSetting;
import com.morkaz.moxplayerparticles.data.PlayerData;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DataManager {

	private MoxPlayerParticles main;
	//          ID    , Data
	private Map<String, PlayerData> playerDataMap = new HashMap<>();
	//          Name  , Data
	private Map<String, ParticleSetting> particleSettingMap = new HashMap<>(); // String to give ability to define own particle settings

	public DataManager(MoxPlayerParticles main) {
		this.main = main;
		reload();
	}

	public void reload(){
		this.loadParticlesData();
		this.loadAllPlayersData();
	}

	public void loadParticlesData(){
		particleSettingMap.clear();
		// Define default particle data for all particle types
		for (Particle particle : Particle.values()){
			ParticleSetting particleSetting = new ParticleSetting(
					particle.toString(),
					main.getConfig().getString("permission-schema").replace("%particle%", particle.toString()),
					particle,
					null,
					main.getParticlesConfig().getInt("default-setting.amount"),
					main.getParticlesConfig().getDouble("default-setting.offset-X"),
					main.getParticlesConfig().getDouble("default-setting.offset-Y"),
					main.getParticlesConfig().getDouble("default-setting.offset-Z"),
					main.getParticlesConfig().getDouble("default-setting.extra"),
					main.getParticlesConfig().get("default-setting.data")
			);
			this.particleSettingMap.put(particle.toString(), particleSetting);
		}
		// Defined own particles data in config
		ConfigurationSection indexes =  main.getParticlesConfig().getConfigurationSection("particles");
		for (String index : indexes.getKeys(false)){
			Particle particle = Particle.valueOf(main.getParticlesConfig().getString("particles."+index+".particle"));
			Double offsetX = main.getParticlesConfig().getDouble("particles."+index+".offset-X");
			Double offsetY = main.getParticlesConfig().getDouble("particles."+index+".offset-Y");
			Double offsetZ = main.getParticlesConfig().getDouble("particles."+index+".offset-Z");
			Integer amount = main.getParticlesConfig().getInt("particles."+index+".amount");
			Double extra = main.getParticlesConfig().getDouble("particles."+index+".extra");
			Object data = main.getParticlesConfig().get("particles."+index+".data");
			ParticleSetting particleSetting = new ParticleSetting(
					index,
					main.getConfig().getString("permission-schema").replace("%particle%", particle.toString()),
					particle,
					null,
					amount,
					offsetX,
					offsetY,
					offsetZ,
					extra,
					data
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
