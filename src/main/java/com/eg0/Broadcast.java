package com.eg0;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class Broadcast implements Runnable {
	
	private String broadcastIP = "";

	@Override
	public void run() {
		while (WindowsMixer.inSearch) {
			try {
				broadcastIP = getBroadcast();
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

	  public static String getBroadcast(){
		    String found_bcast_address=null;
		     System.setProperty("java.net.preferIPv4Stack", "true"); 
		        try
		        {
		          Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces();
		          while (niEnum.hasMoreElements())
		          {
		            NetworkInterface ni = niEnum.nextElement();
		            if(!ni.isLoopback()){
		                for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses())
		                {

		                  found_bcast_address = interfaceAddress.getBroadcast().toString();
		                  found_bcast_address = found_bcast_address.substring(1);

		                }
		            }
		          }
		        }
		        catch (Exception e)
		        {
		          System.out.println(e);
		        }
		        return found_bcast_address;
		}
	
}
