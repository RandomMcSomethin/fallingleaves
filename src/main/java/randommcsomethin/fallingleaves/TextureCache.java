package randommcsomethin.fallingleaves;

import java.util.HashMap;

public class TextureCache {
    public static class Data {
        public final int color;
        public final String resourcePack;

        public Data(int color, String resourcePack) {
            this.color = color;
            this.resourcePack = resourcePack;
        }
    }

    public static final HashMap<String, Data> INST = new HashMap<>();

    private TextureCache() {}
}
