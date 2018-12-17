package com.morkaz.moxplayerparticles.configuration;

import com.morkaz.moxlibrary.api.ConfigUtils;
import com.morkaz.moxlibrary.other.configuration.LocaleConfiguration;
import com.morkaz.moxplayerparticles.MoxPlayerParticles;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager extends LocaleConfiguration {

	private MoxPlayerParticles main;
	private FileConfiguration particlesConfig;

	public ConfigManager(MoxPlayerParticles plugin) {
		super(plugin, "messages", "pl", "locale");
		this.main = plugin;
		this.reload();
	}

	public FileConfiguration getParticlesConfig() {
		return particlesConfig;
	}

	public void reloadParticlesConfig(){
		particlesConfig = ConfigUtils.loadFileConfiguration(main, "particles.yml", false);
	}

	public void reload(){
		super.reloadConfiguration();
		this.reloadParticlesConfig();
	}




}
