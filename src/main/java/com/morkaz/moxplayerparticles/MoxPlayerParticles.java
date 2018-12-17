package com.morkaz.moxplayerparticles;

import com.morkaz.moxlibrary.api.QueryUtils;
import com.morkaz.moxlibrary.api.ServerUtils;
import com.morkaz.moxlibrary.database.sql.SQLDatabase;
import com.morkaz.moxlibrary.database.sql.mysql.MySQLDatabase;
import com.morkaz.moxlibrary.database.sql.sqlite.SQLiteDatabase;
import com.morkaz.moxplayerparticles.commands.PlayerParticlesCommandBody;
import com.morkaz.moxplayerparticles.configuration.ConfigManager;
import com.morkaz.moxplayerparticles.listeners.BlockInteractListener;
import com.morkaz.moxplayerparticles.listeners.EntityDamageListener;
import com.morkaz.moxplayerparticles.listeners.JoinQuitListener;
import com.morkaz.moxplayerparticles.listeners.MoveListener;
import com.morkaz.moxplayerparticles.managers.DataManager;
import com.morkaz.moxplayerparticles.misc.AsyncPlayerDataUpdater;
import com.morkaz.moxplayerparticles.misc.Metrics;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MoxPlayerParticles extends JavaPlugin {

	private static MoxPlayerParticles instance;

	private SQLDatabase database;
	private DataManager dataManager;
	private ConfigManager configManager;
	private AsyncPlayerDataUpdater playerDataUpdater;
	private Metrics metrics;
	public final String
			TABLE = "Players",
			ID_COLUMN = "ID",
			DATA_COLUMN= "ParticleData",
			LAST_LOGIN_COLUMN = "LastLogin"
	;


	@Override
	public void onEnable(){
		//Set Instance
		instance = this;

		//Add Metrics
		metrics = new Metrics(this);

		//Initialize Configuration Manager
		configManager = new ConfigManager(this);

		//Database setup
		if (getConfig().getString("database.type").equalsIgnoreCase("mysql")){
			String host = getConfig().getString("database.settings.mysql.host");
			String port = getConfig().getString("database.settings.mysql.port");
			String databaseName = getConfig().getString("database.settings.mysql.database");
			String user = getConfig().getString("database.settings.mysql.user");
			String password = getConfig().getString("database.settings.mysql.password");
			database = new MySQLDatabase(this);
			database.createConnection(host, port, databaseName, user, password);
		} else {
			String fileLocation = getConfig().getString("database.settings.sqlite.file-location").replace("%plugin-folder%", getDataFolder().getPath());
			database = new SQLiteDatabase(this);
			database.createConnection(fileLocation);
		}

		//New table create
		List<Pair<String, String>> columnTypeList = new ArrayList<>();
		columnTypeList.add(Pair.of(ID_COLUMN, "varchar(36)"));
		columnTypeList.add(Pair.of(DATA_COLUMN, "TEXT"));
		columnTypeList.add(Pair.of(LAST_LOGIN_COLUMN, "BIGINT"));
		String newTableQuery = QueryUtils.constructQueryTableCreate(
				TABLE,
				columnTypeList,
				ID_COLUMN,
				database.getDatabaseType()
		);
		database.updateSync(newTableQuery);

		//Initialize schedulers
		playerDataUpdater = new AsyncPlayerDataUpdater(this);

		//Initialize Managers
		dataManager = new DataManager(this);

		//Register Commands
		PluginCommand command = ServerUtils.registerCommand(
				this,
				"moxplayerparticles",
				Arrays.asList("mpp", "moxpp"),
				"Main command of MoxPlayerParticles",
				"/mpp <args..?>"
		);
		PlayerParticlesCommandBody playerParticlesCommandBody = new PlayerParticlesCommandBody(this);
		command.setExecutor(playerParticlesCommandBody);
		command.setTabCompleter(playerParticlesCommandBody);

		//Initialize Listeners
		new JoinQuitListener(this);
		new EntityDamageListener(this);
		new MoveListener(this);
		new BlockInteractListener(this);

		//Ending
		Bukkit.getLogger().info("["+getDescription().getName()+"] Plugin enabled!");
	}


	@Override
	public void onDisable(){
		//Ending
		Bukkit.getLogger().info("["+getDescription().getName()+"] Plugin disabled!");
	}


	public static MoxPlayerParticles getInstance() {
		return instance;
	}

	public Metrics getMetrics() {
		return metrics;
	}

	public SQLDatabase getDatabase() {
		return database;
	}

	public DataManager getDataManager() {
		return dataManager;
	}

	public ConfigManager getConfigManager() {
		return configManager;
	}

	public FileConfiguration getParticlesConfig(){
		return this.configManager.getParticlesConfig();
	}

	public FileConfiguration getMessagesConfig(){
		return this.configManager.getMessagesConfig();
	}

	public AsyncPlayerDataUpdater getPlayerDataUpdater() {
		return playerDataUpdater;
	}

	public String getPrefix(){
		return this.configManager.getMessagesConfig().getString("misc.prefix");
	}

}
