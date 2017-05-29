package com.eg0;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Handler extends Thread {

	private Socket socket;
	InputStream inputStream;
	ObjectInputStream objectInputStream;
	OutputStream outputStream;
	static ObjectOutputStream objectOutputStream;

	public Handler(Socket socket) {
		this.socket = socket;
	}

	@SuppressWarnings("incomplete-switch")
	public void run() {
		try {
			userObject();
			WindowsMixer.connected();
			WindowsMixer.inSearch = false;
			while (socket.isConnected()) {
				Message message = (Message) objectInputStream.readObject();
				if (message != null) {
					switch (message.getMessageType()) {
					case PC_SENDAPPS:
						WindowsMixer.sliders(message.getApplications());
						break;
					}
				}
			}
		} catch (Exception e) {
			WindowsMixer.disconnected();
			WindowsMixer.startBroadcast();
		}
	}

	public static void setVolume(int id, int volume) {
		Message message = new Message();
		message.setMessageType(MessageType.APP_SETVOLUME);
		message.setId(id);
		message.setVolume(volume);
		try {
			objectOutputStream.writeObject(message);
			objectOutputStream.reset();
			objectOutputStream.flush();
		} catch (Exception e) {
		}
	}

	private void userObject() throws Exception {
		inputStream = socket.getInputStream();
		objectInputStream = new ObjectInputStream(inputStream);
		outputStream = socket.getOutputStream();
		objectOutputStream = new ObjectOutputStream(outputStream);
	}

}