package randommcsomethin.fallingleaves.math;

import org.jetbrains.annotations.Nullable;

import java.util.Random;

/** Probability distribution with minimum value a, maximum value b and most likely value c */
public class TriangularDistribution {
    public final float a, b, c;
    protected final Random rng;
    protected final float f;

    public TriangularDistribution(float a, float b, float c, @Nullable Random rng) {
        if (!(a < b) || !(a <= c && c <= b))
            throw new IllegalArgumentException(String.format("not %f <= %f <= %f", a, b, c));

        this.a = a;
        this.b = b;
        this.c = c;
        this.f = (c - a) / (b - a);
        this.rng = (rng == null ? new Random() : rng);
    }

    public float sample() {
        float u = rng.nextFloat();
        if (u < f) return a + (float)Math.sqrt(      u  * (b - a) * (c - a));
        else       return b - (float)Math.sqrt((1f - u) * (b - a) * (b - c));
    }
}
