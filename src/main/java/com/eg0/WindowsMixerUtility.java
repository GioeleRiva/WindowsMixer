package com.eg0;

import java.awt.Dimension;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class WindowsMixerUtility extends Application {

	public static boolean renaming = false;
	public static boolean selection = false;

	private TrayIcon trayIcon;
	private static AnchorPane anchorPane;
	private static ScrollPane scrollPane;
	private static Pane content;
	private static Label connection;

	@Override
	public void start(Stage stage) throws Exception {

		createTrayIcon(stage);
		Platform.setImplicitExit(false);

		anchorPane = new AnchorPane();
		anchorPane.setPrefSize(640, 360);
		anchorPane.getStylesheets().add("/AppStyle.css");
		anchorPane.setId("disc");
		Scene scene = new Scene(anchorPane);

		createLabel();

		stage.getIcons().add(new Image(WindowsMixer.class.getResourceAsStream("/icon.png")));
		stage.setTitle("Windows Mixer Utility");
		stage.setResizable(false);
		stage.setScene(scene);
		stage.show();

		startReceive();
		
		hide(stage);

	}

	private static void createLabel() {
		connection = new Label("Waiting for connection");
		connection.setPrefSize(300, 30);
		connection.setLayoutX(330);
		connection.setLayoutY(10);
		connection.setId("connection");
		connection.setStyle("-fx-font-size: 15; -fx-background-radius: 5;");
		connection.setAlignment(Pos.CENTER);
		anchorPane.getChildren().add(connection);
	}

	public static void connected() {
		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				return null;
			}
		};
		task.setOnSucceeded(done -> {
			connection.setText("Connected");
			anchorPane.setId("conn");
			scrollPane = new ScrollPane();
			scrollPane.setPrefSize(300, 300);
			scrollPane.setLayoutX(330);
			scrollPane.setLayoutY(50);
			scrollPane.setId("scroll");
			anchorPane.getChildren().add(scrollPane);
			content = new Pane();
			scrollPane.setContent(content);
			scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
			scrollPane.setVbarPolicy(ScrollBarPolicy.NEVER);
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
		task.setOnSucceeded(done -> {
			anchorPane.getChildren().clear();
			createLabel();
			anchorPane.setId("disc");
			try {
				Listener.socket.close();
			} catch (Exception e) {
				System.out.println(e);
			}
		});
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();
	}

	public static void addApplications(ArrayList<MixerApplication> mixerApplications) {
		Task<ArrayList<Pane>> task = new Task<ArrayList<Pane>>() {
			@Override
			protected ArrayList<Pane> call() throws Exception {
				ArrayList<Pane> apps = new ArrayList<>();
				for (int a = 0; a < mixerApplications.size(); a++) {
					AppPaneDesktop appPaneDesktop = new AppPaneDesktop(mixerApplications.get(a));
					appPaneDesktop.setLayoutY(50 * a);
					apps.add(appPaneDesktop);
				}
				return apps;
			}
		};
		task.setOnSucceeded(event -> {
			content.getChildren().clear();
			content.setPrefHeight(50 * task.getValue().size());
			for (int b = 0; b < task.getValue().size(); b++) {
				content.getChildren().add(task.getValue().get(b));
			}
		});
		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();
	}

	public static void startReceive() {
		Receiver receive = new Receiver();
		Thread receiveThread = new Thread(receive);
		receiveThread.start();
	}

	public static void startListener(String IP) {
		Listener listener = new Listener(IP, 15000);
		Thread listenerThread = new Thread(listener);
		listenerThread.start();
	}

	public void createTrayIcon(final Stage stage) {
		if (SystemTray.isSupported()) {

			SystemTray tray = SystemTray.getSystemTray();

			java.awt.Image image = null;
			try {
				ClassLoader classLoader = getClass().getClassLoader();
				URL icon = classLoader.getResource("icon.png");
				Dimension imageSize = tray.getTrayIconSize();
				Double imageWidth = imageSize.getWidth();
				image = ImageIO.read(icon).getScaledInstance(imageWidth.intValue(), imageWidth.intValue(),
						java.awt.Image.SCALE_SMOOTH);
			} catch (Exception e) {
				System.out.println(e);
			}

			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent t) {
					hide(stage);
				}
			});

			final ActionListener closeListener = new ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					System.exit(0);
				}
			};

			ActionListener showListener = new ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							stage.show();
						}
					});
				}
			};

			PopupMenu popup = new PopupMenu();
			MenuItem showItem = new MenuItem("Show");
			showItem.addActionListener(showListener);
			popup.add(showItem);
			MenuItem closeItem = new MenuItem("Close");
			closeItem.addActionListener(closeListener);
			popup.add(closeItem);

			trayIcon = new TrayIcon(image, "Windows Mixer Utility", popup);
			trayIcon.addActionListener(showListener);
			try {
				tray.add(trayIcon);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	private void hide(final Stage stage) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (SystemTray.isSupported()) {
					stage.hide();
				} else {
					System.exit(0);
				}
			}
		});
	}

	public static void main(String[] args) {
		launch(args);
	}

}
