package randommcsomethin.fallingleaves.util;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TextureCache {
    public record Data(double[] color) {
        public Data(double[] color) {
            if (color.length != 3)
                throw new IllegalArgumentException("texture color should have 3 components");

            this.color = Arrays.copyOf(color, color.length);
        }

        public double[] getColor() {
            return Arrays.copyOf(color, color.length);
        }
    }

    public record BiomeBlock(Identifier biomeId, Identifier blockId) { }

    public static final Map<Identifier, Data> INST = new HashMap<>();
    public static final Map<BiomeBlock, Identifier> biomeTextures = new HashMap<>();

    private TextureCache() {}

}
