package com.eg0;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class Broadcast implements Runnable {

	@Override
	public void run() {
		while (WindowsMixer.inSearch) {
			try {
				String temp0 = getLocalIpAddress().replace(".", "#");
				String[] temp = temp0.split("#");
				String broadcastIP = temp[0] + "." + temp[1] + "." + temp[2] + ".255";
				InetAddress host = InetAddress.getByName(broadcastIP);
				DatagramSocket socket = new DatagramSocket(null);
				byte[] buffer = new byte[2048];
				String string = "HELLO I'M PHONE";
				buffer = string.getBytes();
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length, host, 4466);
				socket.send(packet);
				socket.close();
				Thread.sleep(1000);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		return;
	}

	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
						return inetAddress.getHostAddress();
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

}
