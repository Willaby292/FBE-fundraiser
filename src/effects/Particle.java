package effects;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import main.MathUtils;

public class Particle extends Circle {

	protected static final Double	MAX_RADIUS	= 5d;
	protected static final Double	MIN_RADIUS	= 2d;

	protected static final Double TWINKLE_RATE = 5d;

	protected Double	end_x;
	protected Double	end_y;
	protected Double	end_r	= 0d;
	protected Double	ttl		= MathUtils.randRange(0.5d, 1.5d);

	protected Particle() {
		super();
		Double randomRadius = MathUtils.randRange(MIN_RADIUS, MAX_RADIUS);
		Double randomHue = MathUtils.randDegree();
		setRadius(randomRadius);
		setFill(Color.hsb(randomHue, 1, 1, 1));
	}

	protected Particle(Double radius, Double hue) {
		super();
		setRadius(radius);
		setFill(Color.hsb(hue, 1, 1, 1));
	}

	protected void setStartLocation(double centerX, double centerY) {
		setCenterX(centerX);
		setCenterY(centerY);
	}

	protected void setDestination(Double end_x, Double end_y) {
		this.end_x = end_x;
		this.end_y = end_y;
	}

	protected void setTrajectory(Double magnitude, Double direction) {
		this.end_x = getCenterX() + MathUtils.cos(direction) * magnitude;
		this.end_y = getCenterY() + MathUtils.sin(direction) * magnitude;
	}

	protected void launch() {
		final Timeline timeline = new Timeline();
		Interpolator interpolator = new Interpolator() {
			@Override
			protected double curve(double t) {
				double ratioOfCircle = t * Math.PI * 2;
				return Math.cos(ratioOfCircle * TWINKLE_RATE) / (1 + t * t);
			}
		};
		Duration duration = Duration.seconds(ttl);
		KeyValue xKV = new KeyValue(centerXProperty(), end_x);
		KeyValue yKV = new KeyValue(centerYProperty(), end_y);
		KeyValue rKV = new KeyValue(radiusProperty(), end_r, interpolator);
		KeyFrame xKF = new KeyFrame(duration, xKV);
		KeyFrame yKF = new KeyFrame(duration, yKV);
		KeyFrame rKF = new KeyFrame(duration, rKV);
		timeline.getKeyFrames().addAll(xKF, yKF, rKF);
		timeline.setOnFinished(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				terminate();
			}
		});
		timeline.play();
	}

	protected void terminate() {
		Pane pane = (Pane) getParent();
		pane.getChildren().remove(this);
	}

	protected Color getColor() {
		return (Color) getFill();
	}

	protected void setTtl(Double ttl) {
		this.ttl = ttl;
	}

}
