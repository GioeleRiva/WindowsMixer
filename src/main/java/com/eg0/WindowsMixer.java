package com.eg0;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class WindowsMixer extends Application {

	public static boolean inSearch = true;

	static double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
	static double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
	static double textSize = screenHeight / 40;

	static Pane content;
	static Label connection;

	public static ArrayList<MixerApplication> apps;
	public static ArrayList<MixerApplication> appsDrawn = new ArrayList<>();
	public static ArrayList<MixerApplication> appsAvailable;

	@Override
	public void start(Stage stage) {
		AnchorPane anchorPane = new AnchorPane();
		anchorPane.setPrefSize(screenWidth, screenHeight);
		anchorPane.getStylesheets().add("/AppStyle.css");
		anchorPane.setId("background");

		connection = new Label("Waiting for connection");
		connection.setPrefSize(screenWidth - 2 * screenHeight / 20, screenHeight / 20);
		connection.setLayoutX(screenHeight / 20);
		connection.setLayoutY(screenHeight / 20);
		connection.setStyle("-fx-font-size: " + Double.toString(textSize) + "; -fx-background-radius: "
				+ Double.toString(textSize / 4) + ";");
		connection.setId("connection");
		connection.setAlignment(Pos.CENTER);
		anchorPane.getChildren().add(connection);

		content = new Pane();
		content.setMaxSize(screenWidth - 2 * screenHeight / 20, screenHeight - 4 * screenHeight / 20);
		content.setLayoutX(screenHeight / 20);
		content.setLayoutY(3 * screenHeight / 20);
		content.setId("content");
		anchorPane.getChildren().add(content);

		Scene scene = new Scene(anchorPane);
		stage.setScene(scene);
		stage.show();

		startBroadcast();
		startServer();

	}

	public static void connected() {
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				return null;
			}
		};
		task.setOnSucceeded(event -> {
			connection.setText("Connected");
		});
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();
	}

	public static void disconnected() {
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				return null;
			}
		};
		task.setOnSucceeded(event -> {
			connection.setText("Waiting for connection");
			content.getChildren().clear();
		});
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();
	}

	public static void sliders(ArrayList<MixerApplication> applications) {

		apps = applications;
		ArrayList<String> appNames = new ArrayList<>();
		ArrayList<String> appDrawnNames = new ArrayList<>();
		appsAvailable = apps;

		for (int a = 0; a < apps.size(); a++) {
			appNames.add(apps.get(a).getProcessName());
		}

		for (int a = 0; a < appsDrawn.size(); a++) {
			appDrawnNames.add(appsDrawn.get(a).getProcessName());
		}

		for (int i = 0; i < appsDrawn.size(); i++) {
			if (!appNames.contains(appDrawnNames.get(i))) {
				appDrawnNames.remove(i);
				appsDrawn.remove(i);
				i--;
			} else {
				for (int x = 0; x < appsDrawn.size(); x++) {
					for (int y = 0; y < apps.size(); y++) {
						if (appsDrawn.get(x).getProcessName().equals(apps.get(y).getProcessName())
								&& !appsDrawn.get(x).getCustomName().equals(apps.get(y).getCustomName())) {
							appsDrawn.get(x).setCustomName(apps.get(y).getCustomName());
						}
					}
				}

				for (int b = 0; b < appsAvailable.size(); b++) {
					if (appsAvailable.get(b).getProcessName().equals(appsDrawn.get(i).getProcessName())) {
						appsAvailable.remove(b);
					}
				}
			}
		}

		int toAdd = appsDrawn.size();
		if (appsDrawn.size() < 4) {
			for (int c = 0, d = 0; c < (4 - toAdd); c++, d++) {
				try {
					appsDrawn.add(appsAvailable.get(d));
					appsAvailable.remove(d);
					d--;

				} catch (Exception e) {
					// System.out.println(e);
				}
			}
		}

		drawSliders();

	}

	private static void drawSliders() {

		Task<ArrayList<Pane>> task = new Task<ArrayList<Pane>>() {
			@Override
			protected ArrayList<Pane> call() throws Exception {

				ArrayList<Pane> sliders = new ArrayList<Pane>();
				for (int i = 0; i < appsDrawn.size(); i++) {
					AppPane appPane = new AppPane(appsDrawn.get(i));
					appPane.setLayoutX(((screenWidth - 2 * screenHeight / 20) / 4) * i);
					sliders.add(appPane);
				}
				return sliders;
			}
		};
		task.setOnSucceeded(event -> {
			content.getChildren().clear();
			for (int y = 0; y < task.getValue().size(); y++) {
				content.getChildren().add(task.getValue().get(y));
			}
		});
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();
	}

	public static void startBroadcast() {
		inSearch = true;
		Broadcast broadcast = new Broadcast();
		Thread broadcastThread = new Thread(broadcast);
		broadcastThread.start();
	}

	public static void startServer() {
		Server server = new Server();
		Thread serverThread = new Thread(server);
		serverThread.start();
	}

}