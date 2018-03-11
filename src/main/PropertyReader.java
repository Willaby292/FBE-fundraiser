package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javafx.scene.paint.Color;

public class PropertyReader {

	private PropertyReader() {
	}

	private static String		source		= "settings.txt";
	private static Properties	properties	= new Properties();;

	public static Color	progressBarBackgroundColor;
	public static Color	progressBarFillColor;

	public static void load() {
		try {
			FileInputStream inStream = new FileInputStream(source);
			properties.load(inStream);

			progressBarBackgroundColor = parseColor(properties.getProperty("progress.background.color"));
			progressBarFillColor = parseColor(properties.getProperty("progress.fill.color"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Color parseColor(String input) {
		try {
			return Color.web(input);
		} catch (Exception e) {
			e.printStackTrace();
			return Color.GREY;
		}
	}

	public static Color getProgressBarBackgroundColor() {
		return progressBarBackgroundColor;
	}

	public static Color getProgressBarFillColor() {
		return progressBarFillColor;
	}
}
