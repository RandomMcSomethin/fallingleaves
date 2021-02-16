package randommcsomethin.fallingleaves.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import randommcsomethin.fallingleaves.util.Wind;

import static randommcsomethin.fallingleaves.init.Config.CONFIG;

public class FallingLeafParticle extends SpriteBillboardParticle {

    // how particle age/maxAge works:
    // particles start out with age 0 when constructed
    // tick() is first called when age is 1
    // tick() is last called when age = maxAge + 1 (Mojang, pls fix)
    // therefore, the lifespan of a particle is actually maxAge + 2

    protected static final double DT = 1 / 20.0;
    protected static final int FADE_DURATION = 16; // in ticks

    protected float mass;
    protected float windCoefficient; // to emulate drag/lift

    protected final float rotateFactor;

    protected FallingLeafParticle(ClientWorld clientWorld, double x, double y, double z, double r, double g, double b, SpriteProvider provider) {
        super(clientWorld, x, y - 0.2, z, 0.0, 0.0, 0.0); // temporary collision workaround TODO
        this.setSprite(provider);

        this.mass = 0.08f + (float)Math.random() * 0.04f;
        this.windCoefficient = 0.9f + (float)Math.random() * 0.2f;

        // the Particle constructor adds random noise to the velocity which we don't want
        this.velocityX = 0.0;
        this.velocityY = 0.0;
        this.velocityZ = 0.0;

        this.collidesWithWorld = true; // TODO: is it possible to turn off collisions with leaf blocks?
        this.gravityStrength = mass;
        this.maxAge = CONFIG.leafLifespan;

        this.colorRed   = (float) r;
        this.colorGreen = (float) g;
        this.colorBlue  = (float) b;
        // TODO: the longer the leaves live the faster they spin, which is an issue for long lived leaves
        this.rotateFactor = ((float) Math.random() - 0.5F) * 0.01F;

        this.scale = CONFIG.getLeafSize();

        this.colorAlpha = 0.0F; // will be faded in
    }

    @Override
    public void tick() {
        super.tick();

        // fade-in animation
        if (age <= FADE_DURATION) {
            colorAlpha += 1F / FADE_DURATION;
        }

        // fade-out animation
        if (age >= maxAge + 1 - FADE_DURATION) {
            colorAlpha -= 1F / FADE_DURATION;
        }

        // prevent tick() from being called when age = maxAge + 1 (prevent negative colorAlpha)
        if (age == maxAge)
            this.markDead();

        prevAngle = angle;

        if (world.getFluidState(new BlockPos(x, y, z)).isIn(FluidTags.WATER)) {
            // float on water
            velocityY = 0.0;
            gravityStrength = 0.0F;
        } else {
            // slowly fall to the ground
            gravityStrength = mass;

            // spin when in the air
            if (!onGround) {
                angle += Math.PI * MathHelper.sin(rotateFactor * age) / 2F;
            }

            // TODO: field_21507 inside move() makes particles stop permanently once they fall on the ground
            //       that is nice sometimes, but some/most leaves should still get blown along the ground by the wind
            //       Leaves on water get blown along it, though they barely speed down

            // apply wind force / integrate wind acceleration (the last DT is because velocity is in blocks / tick)
            velocityX += ((Wind.windX * windCoefficient / mass) * DT) * DT;
            velocityZ += ((Wind.windZ * windCoefficient / mass) * DT) * DT;
        }
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class DefaultFactory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider provider;

        public DefaultFactory(SpriteProvider provider) {
            this.provider = provider;
        }

        @Override
        public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new FallingLeafParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.provider);
        }
    }
}
