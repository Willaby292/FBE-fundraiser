package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.activation.UnsupportedDataTypeException;

import javafx.scene.paint.Color;

public class PropertyReader {

	private PropertyReader() {
	}

	private static final String		source		= "settings.txt";
	private static final Properties	properties	= new Properties();;

	private static final Set<Property<?>> PROPERTIES = new HashSet<>();

	public static final Property<Color>		BACKGROUND_COLOR	= new Property<>("progress.background.color",
			Color.GREEN);
	public static final Property<Color>		FILL_COLOR			= new Property<>("progress.fill.color", Color.SILVER);
	public static final Property<Double>	SIZE_WIDTH			= new Property<>("progress.size.width", 600d);
	public static final Property<Double>	SIZE_HEIGHT			= new Property<>("progress.size.height", 600d);

	public static void reload() {
		try {
			FileInputStream inStream = new FileInputStream(source);
			properties.load(inStream);

			for (Property<?> property : PROPERTIES) {
				property.readFrom(properties);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private static <Type> Type parse(String input, Type default_value) {
		try {
			boolean assignableFromColor = default_value.getClass().isAssignableFrom(Color.class);
			if (assignableFromColor) {
				return (Type) Color.web(input);
			}
			boolean assignableFromDouble = default_value.getClass().isAssignableFrom(Double.class);
			if (assignableFromDouble) {
				return (Type) new Double(Double.parseDouble(input));
			}
			throw new UnsupportedDataTypeException(
					"Type=" + default_value.getClass() + " parsing has not yet been implemented.");
		} catch (Exception e) {
			e.printStackTrace();
			return default_value;
		}
	}

	public static class Property<Type> {

		private final String	key;
		private Type			value;
		private Type			defaultValue;

		public Property(String key, Type defualtValue) {
			this.key = key;
			this.value = defualtValue;
			this.defaultValue = defualtValue;
			PropertyReader.PROPERTIES.add(this);
		}

		public void readFrom(Properties properties) {
			String input = properties.getProperty(key);
			this.value = parse(input, defaultValue);
		}

		public String getKey() {
			return key;
		}

		public Type getValue() {
			return value;
		}

		public Type getDefaultValue() {
			return defaultValue;
		}

	}
}
