package com.eg0;

import java.io.Serializable;

public class MixerApplication implements Serializable {

	private static final long serialVersionUID = -2598126462104582440L;

	private String processName;
	private String customName;
	private int id;
	private double volume;
	private int[][] icon;

	MixerApplication(String processName, String customName, int id, double volume, int[][] icon) {
		this.processName = processName;
		this.customName = customName;
		this.id = id;
		this.volume = volume;
		this.icon = icon;
	}

	public String getProcessName() {
		return processName;
	}

	public String getCustomName() {
		return customName;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}

	public int getId() {
		return id;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public int[][] getIcon() {
		return icon;
	}

	public void setIcon(int[][] icon) {
		this.icon = icon;
	}

}
