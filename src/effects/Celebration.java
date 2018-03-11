package effects;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import main.MathUtils;

public class Celebration {

	private static Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), func -> {
		Integer amount = MathUtils.randInRange(3, 5);
		Firework.launch(amount);
	}));

	static {
		timeline.setCycleCount(Animation.INDEFINITE);
	}

	public static void start() {
		timeline.play();
	}

	public static void stop() {
		timeline.stop();
	}
}
