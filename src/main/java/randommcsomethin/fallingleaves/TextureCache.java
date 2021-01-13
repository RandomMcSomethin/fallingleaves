package randommcsomethin.fallingleaves;

import java.util.HashMap;

public class TextureCache {
    public static class Data {
        public int color;
        public String resourcePack;

        public Data(int color, String resourcePack) {
            this.color = color;
            this.resourcePack = resourcePack;
        }
    }

    public static final HashMap<String, Data> INST = new HashMap<>();

    private TextureCache() {}
}
