package randommcsomethin.fallingleaves.particle;

import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

public class FallingConiferLeafParticle extends FallingLeafParticle {
    public FallingConiferLeafParticle(ClientWorld clientWorld, double x, double y, double z, double r, double g, double b, SpriteProvider provider) {
        super(clientWorld, x, y, z, r, g, b, provider);
    }
}
