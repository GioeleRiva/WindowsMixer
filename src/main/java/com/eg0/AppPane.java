package com.eg0;

import javafx.animation.ScaleTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class AppPane extends Pane {

	double screenWidth = 360;
	double screenHeight = 640;
	double textSize = screenHeight / 40;
	int oldValue = 0;
	MixerApplication mixerApplication;

	public AppPane(MixerApplication mixerApplication) {
		this.mixerApplication = mixerApplication;
		this.getChildren().clear();
		drawPane();
	}

	private void drawPane() {
		double paneWidth = (screenWidth - 2 * screenHeight / 20) / 4;
		this.setPrefSize(paneWidth, screenHeight - 4 * screenHeight / 20);
		this.getStylesheets().add("/AppStyle.css");
		Label name = new Label(mixerApplication.getCustomName());
		name.setPrefSize(paneWidth, screenHeight / 20);
		name.setStyle("-fx-font-size: " + Double.toString(textSize * 0.5) + ";");
		name.setAlignment(Pos.CENTER);
		name.setId("info");
		this.getChildren().add(name);
		Pane icon = new Pane();
		icon.setPrefSize(32, 32);
		icon.setLayoutX((paneWidth - 32) / 2);
		icon.setLayoutY(screenHeight / 20 + 5);
		try {
			WritableImage img = new WritableImage(32, 32);
			PixelWriter w = img.getPixelWriter();
			int data[][] = mixerApplication.getIcon();
			for (int l = 0; l < 32; l++) {
				for (int j = 0; j < 32; j++) {
					w.setArgb(l, j, data[l][j]);
				}
			}
			icon.setBackground(new Background(
					new BackgroundImage(img, null, null, null, new BackgroundSize(32, 32, false, false, false, true))));
		} catch (Exception e) {
		}
		icon.setOnMouseClicked(change -> {
			if (WindowsMixer.appsAvailable.size() != 0) {
				int i = WindowsMixer.appsDrawn.indexOf(mixerApplication);
				WindowsMixer.appsDrawn.remove(mixerApplication);
				WindowsMixer.appsAvailable.add(mixerApplication);
				this.mixerApplication = WindowsMixer.appsAvailable.get(0);
				WindowsMixer.appsDrawn.add(i, mixerApplication);
				WindowsMixer.appsAvailable.remove(0);
				((Pane) icon.getParent()).getChildren().clear();
				ScaleTransition scaleTransition1 = new ScaleTransition(Duration.millis(250), icon);
				scaleTransition1.setByX(0);
				scaleTransition1.setByY(0);
				scaleTransition1.setOnFinished(play2 -> {
					drawPane();
				});
				scaleTransition1.play();
			}
		});
		this.getChildren().add(icon);
		Slider slider = new Slider();
		slider.setPrefSize(paneWidth, screenHeight - 6 * screenHeight / 20 - 42);
		slider.setOrientation(Orientation.VERTICAL);
		slider.setLayoutY(42 + screenHeight / 20);
		slider.setValue(mixerApplication.getVolume());
		slider.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observableValue, Boolean wasChanging,
					Boolean changing) {
				oldValue = (int) slider.getValue();
				if (!changing) {
					Handler.setVolume(mixerApplication.getId(), (int) slider.getValue());
					mixerApplication.setVolume((int) slider.getValue());
				}
			}
		});
		slider.setOnMouseDragged(drag -> {
			if (slider.getValue() - oldValue > 10 || slider.getValue() - oldValue < -10) {
				oldValue = (int) slider.getValue();
				Handler.setVolume(mixerApplication.getId(), (int) slider.getValue());
				mixerApplication.setVolume((int) slider.getValue());
			}
		});
		slider.setOnMouseClicked(clicked -> {
			oldValue = (int) slider.getValue();
			Handler.setVolume(mixerApplication.getId(), (int) slider.getValue());
			mixerApplication.setVolume((int) slider.getValue());
		});
		this.getChildren().add(slider);
	}

}