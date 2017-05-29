package com.eg0;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {

	private static final long serialVersionUID = 8626262087318336144L;
	private MessageType messageType;
	private ArrayList<MixerApplication> applications;
	private int id;
	private double volume;

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	public ArrayList<MixerApplication> getApplications() {
		return applications;
	}

	public void setApplications(ArrayList<MixerApplication> applications) {
		this.applications = applications;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

}