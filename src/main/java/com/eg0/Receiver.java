package com.eg0;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Receiver implements Runnable {

	public static DatagramSocket socket;

	@Override
	public void run() {
		try {
			socket = new DatagramSocket(4466);
			byte[] buffer = new byte[2048];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);
			String received = packet.getAddress().getHostAddress();
			System.out.println(received);
			WindowsMixerUtility.startListener(received);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
