package randommcsomethin.fallingleaves.util;

import net.minecraft.util.math.MathHelper;

import java.util.Random;

/** A 1-dimensional smooth noise function that works only when called with increasing values of t. */
public class SmoothNoise {

    /** Smoothly goes from 0 to 1 when x increases from 0 and 1 */
    public static double smoothstep(double x) {
        x = MathHelper.clamp(x, 0.0, 1.0);
        return x * x * (3 - 2 * x);
    }

    protected final Random rng = new Random();

    protected int lastLeft = 0;

    // values to interpolate between (leftNoise is the value at left while rightNoise is the value at left + 1)
    protected double leftNoise = 0;
    protected double rightNoise = nextRandom();

    public static final double VAR = 2;
    public static final double OFF = -1;
    protected double lastRandom;

    protected double nextRandom() {
//        return 2 * rng.nextDouble() - 1;

        double newRandom;

        double y = rng.nextDouble();

        // the smaller lastRandom is, the likelier we drift towards 0
        if (y < 0.4 - lastRandom) {
            newRandom = lastRandom / 2.0;
        } else {
            newRandom = VAR * rng.nextDouble() + OFF; // maybe use rng.nextGaussian() instead?
        }

        lastRandom = newRandom;
        return newRandom;
    }

    public double apply(double t) {
        if (t < 0) return 0;

        // left bound of the unit interval t is in
        int left = (int)t;

        // t moved to the next unit interval, so we update the noise values accordingly
        if (left != lastLeft) {
            leftNoise = rightNoise;
            rightNoise = nextRandom();
            lastLeft = left;
        }

        double x = t - left; // in [0, 1]

        // smoothly interpolate between the left and right noise values
        return leftNoise + smoothstep(x) * (rightNoise - leftNoise);
    }
}
