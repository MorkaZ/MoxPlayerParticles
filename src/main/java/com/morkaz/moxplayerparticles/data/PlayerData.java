package com.morkaz.moxplayerparticles.data;


import com.morkaz.moxlibrary.api.QueryUtils;
import com.morkaz.moxlibrary.other.moxdata.MoxData;
import com.morkaz.moxplayerparticles.MoxPlayerParticles;
import com.morkaz.moxplayerparticles.misc.EffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerData {

	//----------------
	// FIELDS
	//----------------

	private Map<EffectType, ParticleSetting> effectsDataMap = new HashMap(); // All effect types and particle datas in pairs.
	private String playerID;
	private MoxData moxPlayerData;
	private Boolean isInDatabase = false;
	private Long lastLogin = System.currentTimeMillis();


	//----------------
	// CONSTRUCTORS
	//----------------

	public PlayerData(String playerID, Long lastLogin, MoxData playerData, Boolean isInDatabase) {
		this.playerID = playerID;
		this.moxPlayerData = playerData;
		this.lastLogin = lastLogin;
		if (isInDatabase == null){
			scanDatabaseForPlayer();
		} else {
			this.isInDatabase = isInDatabase;
		}
		mapValuesFromPlayerData();
	}

	public PlayerData(String playerID, MoxData playerData){
		this.playerID = playerID;
		this.moxPlayerData = playerData;
		scanDatabaseForPlayer();
		mapValuesFromPlayerData();
	}


	//----------------
	// GETTERS & SETTERS
	//----------------

	public String getPlayerID() {
		return playerID;
	}

	public ParticleSetting getParticleSetting(EffectType effectType){
		ParticleSetting particleSetting = this.effectsDataMap.get(effectType);
		if (particleSetting != null){
			return new ParticleSetting(particleSetting);
		}
		return null;
	}

	public Long getLastLogin() {
		return lastLogin;
	}

	public Boolean isInDatabase() {
		return isInDatabase;
	}


	//----------------
	// METHODS
	//----------------

	public void setParticleEffect(EffectType effectType, ParticleSetting particleSetting){
		if (particleSetting == null){
			removeParticleEffect(effectType);
			return;
		}
		this.effectsDataMap.put(effectType, particleSetting);
		this.moxPlayerData.set(effectType.toString(), particleSetting.getParticleIndex());
		this.updateDatabase();
	}

	public void removeParticleEffect(EffectType effectType){
		if (effectsDataMap.containsKey(effectType)){
			effectsDataMap.remove(effectType);
			this.moxPlayerData.remove(effectType.toString());
			this.updateDatabase();
		}
	}

	private void scanDatabaseForPlayer(){
		final MoxPlayerParticles main = MoxPlayerParticles.getInstance();
		new BukkitRunnable(){
			public void run() {
				String query = QueryUtils.constructQueryRowGet(
						main.TABLE,
						main.ID_COLUMN,
						playerID,
						main.getDatabase().getDatabaseType()
				);
				ResultSet resultSet = main.getDatabase().getResult(query);
				try{
					while (resultSet.next()){
						isInDatabase = true;
						lastLogin = resultSet.getLong(main.LAST_LOGIN_COLUMN);
						return;
					}
					lastLogin = System.currentTimeMillis();
					isInDatabase = false;
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(main);
	}

	private void mapValuesFromPlayerData(){
		MoxPlayerParticles main = MoxPlayerParticles.getInstance();
		for (EffectType effectType : EffectType.values()){
			String particleDataName = (String) moxPlayerData.get(effectType.toString()); //Get last used particleData for this effect type
			if (particleDataName == null){
				effectsDataMap.put(effectType, null);
			} else {
				ParticleSetting particleSetting = main.getDataManager().getParticleSetting(particleDataName);
				effectsDataMap.put(effectType, particleSetting);
			}
		}
	}

	private void updateDatabase(){
		MoxPlayerParticles main = MoxPlayerParticles.getInstance();
		if (this.moxPlayerData.isEmpty()){
			if (isInDatabase){
				String deleteQuery = QueryUtils.constructQueryRowRemove(
						main.TABLE,
						main.ID_COLUMN,
						playerID,
						main.getDatabase().getDatabaseType()
				);
				main.getPlayerDataUpdater().scheduleQuery(deleteQuery);
			}
			return;
		}
		List<Pair<String, Object>> pairsList = new ArrayList<>();
		pairsList.add(Pair.of(main.ID_COLUMN, playerID));
		pairsList.add(Pair.of(main.DATA_COLUMN, moxPlayerData.toString()));
		pairsList.add(Pair.of(main.LAST_LOGIN_COLUMN, System.currentTimeMillis()));
		List<String> queries = QueryUtils.constructQueryMultipleValuesSet(
				main.TABLE,
				pairsList,
				true,
				main.getDatabase().getDatabaseType()
		);
		for (String query : queries){
			main.getPlayerDataUpdater().scheduleQuery(query);
		}

	}

}
