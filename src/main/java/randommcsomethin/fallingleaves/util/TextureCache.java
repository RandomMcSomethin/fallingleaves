package randommcsomethin.fallingleaves.util;

import net.minecraft.util.Identifier;

import java.util.HashMap;

public class TextureCache {
    public static final class Data {
        public final int color;
        public final String resourcePack;

        public Data(int color, String resourcePack) {
            this.color = color;
            this.resourcePack = resourcePack;
        }
    }

    public static final HashMap<Identifier, Data> INST = new HashMap<>();

    private TextureCache() {}

}
