package com.morkaz.moxplayerparticles.data;

import com.morkaz.moxlibrary.data.ParticleData;
import org.bukkit.Location;
import org.bukkit.Particle;

public class ParticleSetting extends ParticleData {

	private String particleIndex;
	private String permission;

	public ParticleSetting(ParticleSetting particleSetting){
		super(particleSetting);
		if (particleSetting == null){
			return;
		}
		this.particleIndex = particleSetting.getParticleIndex();
		this.permission = particleSetting.getPermission();
	}

	public ParticleSetting(String particleIndex, String permission, ParticleData particleData) {
		super(particleData);
		this.particleIndex = particleIndex;
		this.permission = permission;
	}

	public ParticleSetting(String particleIndex, String permission, Particle particle, Location location, Integer count, Double offsetX, Double offsetY, Double offsetZ, Double extra, Object data) {
		super(particle, location, count, offsetX, offsetY, offsetZ, extra, data);
		this.particleIndex = particleIndex;
		this.permission = permission;
	}

	public ParticleSetting(String particleIndex, String permission, Particle particle, Location location, Integer count, Double offsetX, Double offsetY, Double offsetZ, Double extra) {
		super(particle, location, count, offsetX, offsetY, offsetZ, extra);
		this.particleIndex = particleIndex;
		this.permission = permission;
	}

	public ParticleSetting(String particleIndex, String permission, Particle particle, Location location, Integer count, Double offsetX, Double offsetY, Double offsetZ) {
		super(particle, location, count, offsetX, offsetY, offsetZ);
		this.particleIndex = particleIndex;
		this.permission = permission;
	}

	public ParticleSetting(String particleIndex, String permission, Particle particle, Location location, Integer count) {
		super(particle, location, count);
		this.particleIndex = particleIndex;
		this.permission = permission;
	}

	public ParticleSetting(String particleIndex, String permission, Particle particle, Location location) {
		super(particle, location);
		this.particleIndex = particleIndex;
		this.permission = permission;
	}

	public ParticleSetting(String particleIndex, String permission, Particle particle) {
		super(particle);
		this.particleIndex = particleIndex;
		this.permission = permission;
	}


	public String getParticleIndex() {
		return particleIndex;
	}

	public void setParticleIndex(String particleIndex) {
		this.particleIndex = particleIndex;
	}

	public ParticleData asParticleData(){
		return super.getThis();
	}

	public String getPermission() {
		return permission;
	}
}
