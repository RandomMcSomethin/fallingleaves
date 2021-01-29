package randommcsomethin.fallingleaves.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import randommcsomethin.fallingleaves.util.SmoothNoise;

import static randommcsomethin.fallingleaves.init.Config.CONFIG;

public class FallingLeafParticle extends SpriteBillboardParticle {

    // how particle age/maxAge works:
    // particles start out with age 0 when constructed
    // tick() is first called when age is 1
    // tick() is last called when age = maxAge + 1 (Mojang, pls fix)
    // therefore, the lifespan of a particle is actually maxAge + 2

    private static final double AIR_DENSITY = 1.2041;
    private static final double DT = 1 / 20.0;

    private static final double DRAG_COEFFICIENT = 0.1;
    private static final float GRAVITY_STRENGTH = 0.1F;
    private static final int FADE_DURATION = 16; // in ticks

    private final float rotateFactor;

    // noise functions used for global wind
    private static final SmoothNoise xNoise = new SmoothNoise();
    private static final SmoothNoise zNoise = new SmoothNoise();
    private static int windTicks = 0;
    private static double windX, windZ;

    public static void tickWind() {
        windTicks++;

        // calculate wind force
        windX = xNoise.apply((windTicks * DT) / 3.0) / 10.0;
        windZ = zNoise.apply((windTicks * DT) / 3.0) / 10.0;
    }

    protected FallingLeafParticle(ClientWorld clientWorld, double x, double y, double z, double r, double g, double b, SpriteProvider provider) {
        super(clientWorld, x, y - 0.2, z, 0.0, 0.0, 0.0); // TODO: temporary collision workaround
        this.setSprite(provider);

        // the Particle constructor adds random noise to the velocity which we don't want
        this.velocityX = 0.0;
        this.velocityY = 0.0;
        this.velocityZ = 0.0;

        this.collidesWithWorld = true; // TODO: is it possible to turn off collisions with leaf blocks?
        this.gravityStrength = GRAVITY_STRENGTH;
        this.maxAge = CONFIG.leafLifespan;

        this.colorRed   = (float) r;
        this.colorGreen = (float) g;
        this.colorBlue  = (float) b;
        // TODO: the longer the leaves live the faster they spin, which is an issue for long lived leaves
        this.rotateFactor = ((float) Math.random() - 0.5F) * 0.01F;

        this.scale = CONFIG.getLeafSize();

        this.colorAlpha = 0.0F; // will be faded in

        // debug
//        this.gravityStrength = 0.0F;
//        this.maxAge = 20000;
    }

    public void tick() {
        super.tick();

        // fade-in animation
        if (this.age <= FADE_DURATION) {
            this.colorAlpha += 1F / FADE_DURATION;
        }

        // fade-out animation
        if (this.age >= this.maxAge + 1 - FADE_DURATION) {
            this.colorAlpha -= 1F / FADE_DURATION;
        }

        // prevent tick() from being called when age = maxAge + 1 (prevent negative colorAlpha)
        if (this.age == this.maxAge)
            this.markDead();

        this.prevAngle = this.angle;

        if (this.world.getFluidState(new BlockPos(this.x, this.y, this.z)).isIn(FluidTags.WATER)) {
            // float on water
            this.velocityY = 0.0;
            this.gravityStrength = 0.0F;
        } else {
            // slowly fall to the ground
            this.gravityStrength = GRAVITY_STRENGTH;

            // spin when in the air
            if (!this.onGround) {
                this.angle += Math.PI * MathHelper.sin(this.rotateFactor * this.age) / 2F;
            }

            // apply wind force
            this.velocityX += windX * DT;
            this.velocityZ += windZ * DT;

            // calculate drag force (air resistance)
            double windNormSq = windX*windX + windZ*windZ; // |wind|²
            double windInvNorm = MathHelper.fastInverseSqrt(windNormSq); // 1 / |wind|
            double area = CONFIG.getLeafSize();

            double dragMagnitude = DRAG_COEFFICIENT * area * 1 / 2.0 * AIR_DENSITY * windNormSq;

            // drag force points in the opposite direction of the wind
            double dragX = -(windX * windInvNorm) * dragMagnitude;
            double dragZ = -(windZ * windInvNorm) * dragMagnitude;

            // apply drag force
            this.velocityX += dragX * DT;
            this.velocityZ += dragZ * DT;
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
