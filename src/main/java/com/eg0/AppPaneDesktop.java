package com.eg0;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;

public class AppPaneDesktop extends Pane {

	public AppPaneDesktop(MixerApplication mixerApplication) {
		this.setPrefSize(300, 50);
		this.getStylesheets().add("/AppStyle.css");

		Pane icon = new Pane();
		icon.setPrefSize(32, 32);
		icon.setLayoutX(10);
		icon.setLayoutY(9);
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
					new BackgroundImage(img, null, null, null, new BackgroundSize(64, 64, false, false, false, true))));
		} catch (Exception e) {
			System.out.println(e);
		}
		this.getChildren().add(icon);

		Label name = new Label(mixerApplication.getCustomName());
		name.setPrefSize(238, 40);
		name.setLayoutX(52);
		name.setLayoutY(5);
		name.setId("name");
		
		Tooltip tooltip = new Tooltip("Click here to rename the application");
		name.setTooltip(tooltip);
		
		name.setOnMouseClicked(rename -> {
			if (!WindowsMixerUtility.selection && mixerApplication.getId()!=0 && mixerApplication.getId()!=4) {
				WindowsMixerUtility.selection = true;
				WindowsMixerUtility.renaming = true;
				TextField textField = new TextField("Insert new name");
				textField.setPrefSize(238, 40);
				textField.setLayoutX(52);
				textField.setLayoutY(5);
				textField.setId("name");
				textField.setOnAction(renamed -> {
					if (!textField.getText().equals("") && !textField.getText().equals("Insert new name") && !textField.getText().contains("#")) {

						try {
							write(mixerApplication.getProcessName(), textField.getText());
						} catch (Exception e) {
							System.out.println(e);
						}
						name.setText(textField.getText());
						this.getChildren().remove(textField);
						WindowsMixerUtility.renaming = false;
						WindowsMixerUtility.selection = false;
					} else {
						this.getChildren().remove(textField);
						WindowsMixerUtility.renaming = false;
						WindowsMixerUtility.selection = false;
					}

				});
				this.getChildren().add(textField);
			}
		});
		this.getChildren().add(name);

	}

	private void write(String process, String newName) throws Exception {
		String tempPath = System.getenv("APPDATA");
		String path = tempPath.replace("Roaming", "Local\\MixerUtility\\Applications.txt");
		File file = new File(path);
		LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(file));
		lineNumberReader.skip(Long.MAX_VALUE);
		long lines = lineNumberReader.getLineNumber();
		lineNumberReader.close();
		@SuppressWarnings("resource")
		BufferedReader in = new BufferedReader(new FileReader(file));
		String reWrite = "";
		if (file.length() != 0) {
			int a = 0;
			for (int i = 0; i < lines; i++) {
				String tempApp = in.readLine();
				if (tempApp.contains(process)) {
					reWrite = reWrite + process + "#" + newName + "\r\n";
					a++;
				} else {
					reWrite = reWrite + tempApp + "\r\n";
				}
			}
			if (a==0){
				reWrite = reWrite + process + "#" + newName + "\r\n";
			}
		} else {
			reWrite = reWrite + process + "#" + newName + "\r\n";
		}
		FileWriter writer = new FileWriter(file);
		writer.write(reWrite);
		writer.close();
	}

}
