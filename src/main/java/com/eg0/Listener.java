package com.eg0;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

public class Listener implements Runnable {

	private String serverIP;
	private int port;

	public static Socket socket;
	private OutputStream outputStream;
	private static ObjectOutputStream objectOutputStream;
	private InputStream inputStream;
	private ObjectInputStream objectInputStream;

	static String temp0;
	static String temp1;
	static String temp3;
	static String temp2;

	static char u = '"';

	private static ArrayList<MixerApplication> applicationsList;

	public Listener(String serverIP, int port) {
		this.serverIP = serverIP;
		this.port = port;
	}

	@SuppressWarnings({ "incomplete-switch", "unused" })
	@Override
	public void run() {
		try {
			temp0 = ClassLoader.getSystemClassLoader().getResource(".").getPath();
			temp1 = temp0.substring(1, temp0.length());
			temp2 = u + temp1.replace("/", "\\");
			temp3 = temp2.replace("%20", " ");
			socket = new Socket(serverIP, port);
			outputStream = socket.getOutputStream();
			objectOutputStream = new ObjectOutputStream(outputStream);
			inputStream = socket.getInputStream();
			objectInputStream = new ObjectInputStream(inputStream);
			refresh();
			Receiver.socket.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		try {
			while (socket.isConnected()) {
				Message message = (Message) objectInputStream.readObject();
				if (message != null) {
					switch (message.getMessageType()) {
					case APP_SETVOLUME:
						if (message.getId() == 4) {
							Process process = Runtime.getRuntime().exec(
									temp3 + "MixerUtility.exe" + u + " setsystemvolume " + (int) message.getVolume());
						} else {
							Process process = Runtime.getRuntime().exec(temp3 + "MixerUtility.exe" + u
									+ " setappvolume " + message.getId() + " " + (int) message.getVolume());
						}
						break;
					}
				}

			}
		} catch (Exception e) {
			System.out.println(e);
			applicationsList.clear();
			WindowsMixerUtility.disconnected();
			WindowsMixerUtility.startReceive();
			return;
		}
	}

	private void refresh() {
		if (!socket.isClosed()) {
			WindowsMixerUtility.selection = true;
			Task<Void> task = new Task<Void>() {
				@Override
				protected Void call() throws Exception {
					if (!WindowsMixerUtility.renaming) {
						sendApps();
						Thread.sleep(10000);
					}
					return null;
				}
			};
			task.setOnSucceeded(done -> {
				refresh();
			});
			Thread thread = new Thread(task);
			thread.setDaemon(true);
			thread.start();
		} else {
			return;
		}
	}

	private static int[][] makeIcon(String appName) {
		Image image = null;
		if (appName.equals("sounds")) {
			image = new Image(Listener.class.getResourceAsStream("/sound.png"));
		} else {
			if (appName.equals("system")) {
				image = new Image(Listener.class.getResourceAsStream("/system.png"));
			} else {
				String tempPath = System.getenv("APPDATA");
				String path = tempPath.replace("Roaming", "Local\\MixerUtility\\" + appName + ".bmp");
				image = new Image("file:" + path);
			}
		}

		int[][] data = new int[32][32];

		PixelReader r = image.getPixelReader();
		for (int i = 0; i < 32; i++) {
			for (int j = 0; j < 32; j++) {
				data[i][j] = r.getArgb(i, j);
			}
		}
		return data;

	}

	public static void sendApps() throws Exception {

		String temp0Path = System.getenv("APPDATA");
		String temp1Path = temp0Path.replace("Roaming", "Local\\");
		String path = temp1Path.replace("%20", " ");

		applicationsList = new ArrayList<>();

		Process process2 = Runtime.getRuntime().exec(temp3 + "MixerUtility.exe" + u + " getsystemvolume");
		InputStream inputStream2 = process2.getInputStream();
		InputStreamReader inputStreamReader2 = new InputStreamReader(inputStream2);
		BufferedReader bufferedReader2 = new BufferedReader(inputStreamReader2);
		String string2 = bufferedReader2.readLine();

		Process process1 = Runtime.getRuntime().exec(temp3 + "MixerUtility.exe" + u + " getappvolume 0");
		InputStream inputStream1 = process1.getInputStream();
		InputStreamReader inputStreamReader1 = new InputStreamReader(inputStream1);
		BufferedReader bufferedReader1 = new BufferedReader(inputStreamReader1);
		String string1 = bufferedReader1.readLine();

		applicationsList
				.add(new MixerApplication("system", "System Volume", 4, Integer.valueOf(string2), makeIcon("system")));
		applicationsList
				.add(new MixerApplication("sounds", "System Sounds", 0, Integer.valueOf(string1), makeIcon("sounds")));

		ArrayList<String> apps = new ArrayList<>();
		ArrayList<String> appNames = new ArrayList<>();

		@SuppressWarnings("unused")
		Process process0 = Runtime.getRuntime().exec(temp3 + "MixerUtility.exe" + u + " getmixerapps");
		Thread.sleep(500);

		String temp2Path = path + "\\MixerUtility\\Applications.txt";
		File file = new File(temp2Path);
		file.createNewFile();
		LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(file));
		lineNumberReader.skip(Long.MAX_VALUE);
		long lines = lineNumberReader.getLineNumber();
		lineNumberReader.close();
		@SuppressWarnings("resource")
		BufferedReader in = new BufferedReader(new FileReader(file));
		try {
			for (int i = 0; i < lines; i++) {
				String tempApp = in.readLine();
				if (!tempApp.equals(null)) {
					String[] appName = tempApp.split("#");
					apps.add(appName[0]);
					appNames.add(appName[1]);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		Process process = Runtime.getRuntime().exec(temp3 + "MixerUtility.exe" + u + " getmixerapps");
		Thread.sleep(1000);
		InputStream inputStream = process.getInputStream();
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String string = null;
		try {
			while (!(string = bufferedReader.readLine()).equals(null)) {
				String[] details = string.split("#");
				String processName = details[0];
				String customName = details[1];

				for (int i = 0; i < apps.size(); i++) {
					if (processName.equals(apps.get(i))) {
						customName = appNames.get(i);
					}
				}

				int id = Integer.valueOf(details[2]);
				double volume = Double.valueOf(details[3]);

				MixerApplication application = new MixerApplication(processName, customName, id, volume,
						makeIcon(processName));
				applicationsList.add(application);
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		WindowsMixerUtility.connected();
		WindowsMixerUtility.addApplications(applicationsList);

		Message message = new Message();
		message.setMessageType(MessageType.PC_SENDAPPS);
		message.setApplications(applicationsList);
		try {
			objectOutputStream.writeObject(message);
			objectOutputStream.flush();
		} catch (Exception e) {
			System.out.println(e);
		}
		WindowsMixerUtility.selection = false;
	}

}