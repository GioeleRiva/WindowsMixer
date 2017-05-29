package com.eg0;

import java.net.ServerSocket;

public class Server implements Runnable {

	private static int port = 15000;

	@Override
	public void run() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
		} catch (Exception e) {
		}
		try {
			while (true) {
				new Handler(serverSocket.accept()).start();
			}
		} catch (Exception e) {
		} finally {
			try {
				serverSocket.close();
			} catch (Exception e) {
			}
		}
	}

}