package fallingleaves.fallingleaves;

import net.minecraft.client.texture.Sprite;

import java.awt.*;
import java.awt.image.BufferedImage;

public class LeafUtils {
    public static Color averageColor(BufferedImage image, int width, int height) {
        long r = 0;
        long g = 0;
        long b = 0;
        int n = 0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x, y);
                if ((rgb >> 24 & 255) == 255) {
                    Color c = new Color(rgb);
                    r += c.getRed();
                    g += c.getGreen();
                    b += c.getBlue();
                    n++;
                }
            }
        }
        //System.out.println(r/n);
        //System.out.println(g/n);
        //System.out.println(b/n);
        return new Color((float)r/n/255, (float)g/n/255, (float)b/n/255);
    }

    public static String spriteToTexture(Sprite sprite) {
        String id = sprite.getId().toString();
        String newId = "nah";
        for (int i = 0; i < id.length() - 1; i++) {
            if (id.charAt(i) == ':') {
                newId = id.substring(0, i + 1) + "textures/block" + id.substring(i + 6) + ".png";
                //System.out.println(newId);
            }
        }
        return newId;
    }
}
