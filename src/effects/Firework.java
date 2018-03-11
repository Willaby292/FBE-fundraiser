package effects;

import java.util.HashSet;
import java.util.Set;

import main.MathUtils;

public class Firework extends Particle {

	private static final Double		COLOR_RANGE		= 30d;
	private static final Integer	PAYLOAD_SIZE	= 100;
	private static final Double		EXPLOSION_SIZE	= 50d;

	private Set<Particle> payload;

	public static void launch(int amount) {
		// Make sure we dont spawn too many Fireworks or the app will crash.
		// Limit to 10
		amount = Math.min(amount, 10);
		for (int i = 0; i < amount; i++) {
			Double width = targetPane.getWidth();
			Double height = targetPane.getHeight();
			Firework firework = new Firework();
			Double start_x = MathUtils.randRange(0d, width);
			Double dest_x = MathUtils.randRange(0d, width);
			// Starts fireworks from bottom of screen
			Double start_y = height;
			// Ends Fireworks in upper 4th of screen.
			Double dest_y = MathUtils.randRange(0d, height / 2d);
			firework.setCenterX(start_x);
			firework.setCenterY(start_y);
			firework.setDestination(dest_x, dest_y);
			firework.launch();
		}
	}

	private Firework() {
		super();
		this.end_r = getRadius();
		payload = createPayload();
	}

	private Set<Particle> createPayload() {
		Set<Particle> payload = new HashSet<Particle>();

		for (int i = 0; i < PAYLOAD_SIZE; i++) {
			Double hue = getColor().getHue();
			Particle particle;

			particle = new Particle(getRadius(), hue + MathUtils.randRange(-COLOR_RANGE, COLOR_RANGE));

			payload.add(particle);
		}
		return payload;
	}

	@Override
	protected void terminate() {
		int count = 0;
		for (Particle particle : payload) {
			particle.setStartLocation(getCenterX(), getCenterY());
			Double direction = ((double) count++ / payload.size()) * 360d;
			Double magnitude = getRadius() * EXPLOSION_SIZE;
			particle.setTrajectory(magnitude, direction);
			particle.launch();
		}
		targetPane.getChildren().remove(this);
	}

}
