package main;

import java.util.Random;

public class MathUtils {

	private static final Random RANDOM = new Random();

	public static Double rand() {
		return RANDOM.nextDouble();
	}

	public static Double randRange(Double min, Double max) throws IllegalArgumentException {
		if (max < min) {
			throw new IllegalArgumentException(
					"Parameter min cannot be larger than max. " + "min=" + min + "max=" + max);
		}
		Double range = max.doubleValue() - min.doubleValue();
		return min + (rand() * range);
	}

	public static Integer randInRange(Integer min, Integer max) throws IllegalArgumentException {
		return (int) Math.floor(randRange(min.doubleValue(), max.doubleValue()));
	}

	public static Double randDegree() {
		return randRange(0d, 360d);
	}

	public static Boolean randChance(Double chance) {
		return rand() < chance;
	}

	public static Boolean randBool() {
		return randChance(0.5d);
	}

	public static Double sin(Double direction) {
		return Math.sin(direction / 360d * 2 * Math.PI);
	}

	public static Double cos(Double direction) {
		return Math.cos(direction / 360d * 2 * Math.PI);
	}
}
