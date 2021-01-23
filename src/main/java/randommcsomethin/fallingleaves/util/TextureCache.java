package randommcsomethin.fallingleaves.util;

import net.minecraft.util.Identifier;

import java.util.HashMap;

public class TextureCache {
    public static final class Data {
        private final double[] color;
        public final String resourcePack;

        public Data(double[] color, String resourcePack) {
            if (color.length != 3)
                throw new IllegalArgumentException("texture color should have 3 components");

            this.color = new double[3];
            System.arraycopy(color, 0, this.color, 0, 3);
            this.resourcePack = resourcePack;
        }

        public double[] getColor() {
            double[] copy = new double[3];
            System.arraycopy(color, 0, copy, 0, 3);
            return copy;
        }
    }

    public static final HashMap<Identifier, Data> INST = new HashMap<>();

    private TextureCache() {}

}
