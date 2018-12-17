package com.morkaz.moxplayerparticles.misc;

import com.morkaz.moxlibrary.scheduler.SimpleSQLScheduler;
import com.morkaz.moxplayerparticles.MoxPlayerParticles;

public class AsyncPlayerDataUpdater extends SimpleSQLScheduler {

	public AsyncPlayerDataUpdater(MoxPlayerParticles plugin) {
		super(plugin, plugin.getDatabase(), false);
		super.startScheduler();
	}

}
